# Приложение для управления полицейским участком в аниме ["Акудама Драйв"](https://myanimelist.net/anime/41433/Akudama_Drive?q=akudama&cat=anime)
## Описание | Description
### ru
В городе Кансай обитает огромное количество уголовных правонарушителей, называемых «акудама». Каждому акудама в зависимости от преступления согласно местному уголовному кодексу начисляется тюремный срок и ранг опасности от D до S. Для отлова акудама у полиции есть несколько отделений бойцов: почти неограниченное количество полицейских роботов, вооружённых шокерами; полк рядовых полицейских с табельными пистолетами; три роты спецназа, облачённого в броню и вооружённого автоматическими винтовками; элитный отдел «Палачей», два десятка бойцов которого в совершенстве владеют навыками рукопашного боя и боя с холодным оружием, основным оружием выбирают энергетические мечи. Так же в распоряжении полиции имеется несколько единиц тяжёлой военной техники, не требующей оператора. Каждая единица техники обладает уникальным серийным номером. Живые бойцы, помимо имени и фамилии, имеют уникальные идентификационные номера, звания. Звание может послужить для перевода полицейского из отдела в отдел, если в нём не будет хватать людей. Оно зависит от количества выполненных заданий. Так же требуемое снаряжение, которым бойца можно экипировать, при наличии оного на складе. Каждой единице инвентаря так же соответствует уникальный идентификационный номер. При поступлении в полицию вызова, связанного с акудама, начальник участка может создать задание на поимку правонарушителя, выделив на него первоначальный состав и указав место вызова и ранг опасности. В процессе выполнения задание высший по званию боец имеет возможность запросить подкрепление, при этом ранг опасности задания будет повышен, пока не достигнет максимума или пока задание не будет завершено.
### en
The city of Kansai is full of criminals called "Akudama". For their crimes Akudama are given a sentence and a rank from D to S accordingly to the local law. To confront this danger Kansai Police Department has formed several division of armed troops: robots with shockers, regular policemen with pistols, heavely armed special forces with assault rifles and the elite "Executioners" squad armed with energetic swords. However, every person and piece of equipment counts. Every person and thing has its own id for system to track.  
When a crime is reported the Chief of the Department should create new mission, give it a rank and assign forces to complete it. The equipment is assigned automatically accordingly to the assigned troop's division. When mission in progress, alive policeman with highest rank is appointed as a commander and has an ability to request reinforcements.
By completing the missions policeman is presented with new ranks that gives him an abilty to trasnfer to the next division if places there are available.
## Требования | Requirements
- Java 8+
- PostgreSQL 9.6+
- Application Server (WildFly)
## Настройка | Initial set up
### База данных | Database
#### ru
Для конфигурации используются скрипты из директории [initdb](https://github.com/SuperJaremy/DB-COURSE/tree/master/initdb).  
1. Перейдите в директорию initdb
2. `psql`
3. `\i create.sql`
4. `\i functions.sql`
5. `\i triggers.sql`
6. `\i data.sql`
7. `\i indexes.sql`
#### en
Configuration scripts can be found in [initdb directory](https://github.com/SuperJaremy/DB-COURSE/tree/master/initdb).
1. cd into initdb directory
2. `psql`
3. `\i create.sql`
4. `\i functions.sql`
5. `\i triggers.sql`
6. `\i data.sql`
7. `\i indedxes.sql`
### Приложение | Application
TODO
## Примеры работы | Examples
![Example_1](https://github.com/SuperJaremy/DB-COURSE/blob/master/examples/Screenshot_20220124_023554.png)
![Example_3](https://github.com/SuperJaremy/DB-COURSE/blob/master/examples/Screenshot_20220124_030214.png)
![Example_4](https://github.com/SuperJaremy/DB-COURSE/blob/master/examples/Screenshot_20220124_030409.png)
