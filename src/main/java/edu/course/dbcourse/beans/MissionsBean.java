package edu.course.dbcourse.beans;

import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "missions")
@ViewScoped
public class MissionsBean implements Messaging {

    @ManagedProperty("#{database}")
    private @Setter
    DataBaseBean db;

    private @Getter
    @Setter
    String address;

    private @Getter
    @Setter
    int akudamaId;

    private @Getter
    @Setter
    int commanderId;

    private List<Mission> currentMissions;

    private @Getter
    @Setter
    List<Rank> ranks;

    private @Getter
    List<Mission> currentFrame;

    private @Getter
    List<IdsEntry> policemenIds;

    private @Getter
    List<IdsEntry> machinesIds;

    private @Getter
    boolean canMoveForward;

    private @Getter
    boolean canMoveBackward;

    private @Getter
    int currentPage;

    private static final int MISSIONS_ON_PAGE = 5;

    private Rank rank;

    public String getRank() {
        if (rank == null)
            return "";
        else
            return rank.getRank();
    }

    public void setRank(String _rank) {
        ranks.stream().filter((x) -> x.getRank().equals(_rank)).findFirst()
                .ifPresent(rank1 -> rank = rank1);
    }

    @PostConstruct
    public void init() {
        policemenIds = new ArrayList<>();
        machinesIds = new ArrayList<>();
        ranks = Rank.getAllRanks(db);
        canMoveBackward = false;
        currentPage = 1;
        currentMissions = new ArrayList<>();
        currentFrame = new ArrayList<>();
        Mission mission = null;
        int i = 0;
        for (; i < MISSIONS_ON_PAGE; i++) {
            mission = Mission.getNextMission(db, mission);
            if (mission != null) {
                currentMissions.add(mission);
                currentFrame.add(mission);
            } else break;
        }
        if (i == MISSIONS_ON_PAGE) {
            mission = Mission.getNextMission(db, mission);
            if (mission != null) {
                canMoveForward = true;
                currentMissions.add(mission);
            }
        }
    }

    public void addPoliceman() {
        policemenIds.add(new IdsEntry());
    }

    public void addMachine() {
        machinesIds.add(new IdsEntry());
    }

    public void removePoliceman() {
        if (policemenIds.size() == 0)
            return;
        policemenIds.remove(policemenIds.size() - 1);
    }

    public void removeMachine() {
        if (machinesIds.size() == 0)
            return;
        machinesIds.remove(machinesIds.size() - 1);
    }

    public void createMission() {
        List<Policeman> policemen = new ArrayList<>();
        for (IdsEntry i :
                policemenIds) {
            Policeman policeman = Policeman.getPolicemanById(db, i.value);
            if (policeman == null) {
                addError("Полицейский с ID " + i.value + " не был найден", "");
                return;
            }
            policemen.add(policeman);
        }
        List<Machine> machines = new ArrayList<>();
        for (IdsEntry i : machinesIds) {
            Machine machine = Machine.getMachineById(db, i.value);
            if (machine == null) {
                addError("Машина с ID " + i.value + " не была найдена", "");
                return;
            }
            machines.add(machine);
        }
        Policeman commander = Policeman.getPolicemanById(db, commanderId);
        if (commander == null) {
            addError("Полицейский с ID " + commanderId + " не был найден", "");
            return;
        }
        Akudama akudama = Akudama.getAkudamaById(db, akudamaId);
        if (akudama == null) {
            addError("Акудама с ID " + akudamaId + " не был найден", "");
            return;
        }
        Mission mission = Mission.addNewMission(db, address, commander, rank, akudama);
        if (mission == null) {
            addError("Отсутствует соединение", "");
            return;
        }
        if (!mission.assignPolicemen(db, policemen) || !mission.assignMachines(db, machines)) {
            addError("Отсутствует соединение", "");
            return;
        }
        addInfo("Задание создано", "");
        policemenIds.clear();
        machinesIds.clear();
        rank = null;
        akudamaId = 0;
        commanderId = 0;
        address = null;
        currentMissions.add(mission);
        if (currentMissions.size() > currentPage * MISSIONS_ON_PAGE)
            canMoveForward = true;
    }

    public void endMission(Mission mission) {
        if (!mission.endMission(db)) {
            addError("Отсутствует соединение", "");
            return;
        }
        addInfo("Миссия завершена", "");
        currentMissions.remove(mission);
        currentFrame.remove(mission);
        if (currentFrame.size() == 0)
            moveBackward();
        if (currentPage * MISSIONS_ON_PAGE <= currentMissions.size() && currentFrame.size() < MISSIONS_ON_PAGE) {
            currentFrame.add(currentMissions.get(currentPage * MISSIONS_ON_PAGE - 1));
            canMoveForward = currentPage * MISSIONS_ON_PAGE < currentMissions.size();
        }
    }

    public void moveForward() {
        if (!canMoveForward)
            return;
        if (currentPage * MISSIONS_ON_PAGE >= currentMissions.size() - 1) {
            loadMoreMissions();
        }
        if (currentPage * MISSIONS_ON_PAGE >= currentMissions.size())
            return;
        formNewFrame(currentPage * MISSIONS_ON_PAGE);
        currentPage++;
        canMoveBackward = true;
        canMoveForward = currentMissions.size() > currentPage * MISSIONS_ON_PAGE;
    }

    public void moveBackward() {
        if (!canMoveBackward)
            return;
        currentPage--;
        formNewFrame((currentPage - 1) * MISSIONS_ON_PAGE);
        canMoveBackward = currentPage > 1;
        canMoveForward = currentMissions.size() > currentPage * MISSIONS_ON_PAGE;
    }

    private void loadMoreMissions() {
        Mission mission = currentMissions.get(currentMissions.size() - 1);
        for (int i = 0; i < MISSIONS_ON_PAGE + 1; i++) {
            mission = Mission.getNextMission(db, mission);
            if (mission != null)
                currentMissions.add(mission);
            else break;
        }
    }

    private void formNewFrame(int firstEntryOnPageIndex) {
        currentFrame.clear();
        int missions = Math.min(MISSIONS_ON_PAGE, currentMissions.size() - firstEntryOnPageIndex);
        for (int i = 0; i < missions; i++) {
            currentFrame.add(currentMissions.get(i + firstEntryOnPageIndex));
        }
    }

    public static class IdsEntry {
        private @Getter
        @Setter
        int value;
    }
}
