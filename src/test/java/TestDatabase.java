import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDatabase {

    private static DataBaseBean db;

    @BeforeAll
    static void getDB(){
        db = new DataBaseBean();
        db.setMAX_CONNECTIONS(4);
        db.setUsername("");
        db.setPassword("");
        db.setDataBaseUrl("");
    }

    @Test
    @Order(1)
    public void testDatabaseConnection(){
        Assertions.assertDoesNotThrow(() -> db.init());
    }

    @Test
    @AfterAll
    public static void testDatabaseClosing(){
        Assertions.assertDoesNotThrow(() -> db.destroy());
    }

    @Test
    @Order(2)
    public void testDatabaseExecution(){
        Assertions.assertDoesNotThrow(() -> db.executeStatement("select * from People"));
    }

    @Test
    @Order(3)
    public void testGetEntity(){
        Assertions.assertNotNull(Policeman.getPolicemanById(db, 1));
        Article article = Article.getArticleById(db, 1);
        Assertions.assertNotNull(article);
    }

    @Test
    @Order(4)
    public void testGetEntityList(){
        List<EquipmentType> list = EquipmentType.getAllEquipmentTypes(db);
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(5)
    public void testAddEntity(){
        List<EquipmentType> types = EquipmentType.getAllEquipmentTypes(db);
        Assertions.assertNotNull(types);
        Assertions.assertTrue(Equipment.AddNewEquipment(db, 1001001, types.get(1)));
    }

    @Test
    @Order(6)
    public void testChangeEntity(){
        Equipment eq = Equipment.getEquipmentById(db, 1109);
        Assertions.assertNotNull(eq);
        List<EquipmentStatus> statuses = EquipmentStatus.getAllEquipmentStatuses(db);
        Assertions.assertNotNull(statuses);
        Assertions.assertTrue(Equipment.changeEquipmentStatus(db, eq, statuses.get(1)));
    }

    @Test
    @Order(7)
    public void testMissionWorkflow(){
        Akudama akudama = Akudama.getAkudamaById(db, 176);
        Assertions.assertNotNull(akudama);
        Policeman commander = Policeman.getPolicemanById(db, 1);
        Assertions.assertNotNull(commander);
        Rank rank = Rank.getRankById(db, 5);
        Assertions.assertNotNull(rank);
        Mission mission = Mission.addNewMission(db, "BABAI", commander, rank, akudama);
        Assertions.assertNotNull(mission);
        Policeman reinforcement = Policeman.getPolicemanById(db, 130);
        Assertions.assertNotNull(reinforcement);
        Machine machine = Machine.getMachineById(db, 975);
        Assertions.assertNotNull(machine);
        List<Policeman> police = new ArrayList<>(1);
        police.add(reinforcement);
        List<Machine> machines = new ArrayList<>(1);
        machines.add(machine);
        Assertions.assertTrue(mission.assignPolicemen(db, police));
        Assertions.assertTrue(mission.assignMachines(db, machines));
        Assertions.assertTrue(mission.endMission(db));
    }

}
