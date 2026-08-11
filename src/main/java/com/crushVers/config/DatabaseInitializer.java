package com.crushVers.config;

import com.crushVers.enums.TagGroup;
import com.crushVers.model.*;
import com.crushVers.service.FirestoreService;
import com.crushVers.service.UserRoleService;
import com.crushVers.service.BaseDictionaryService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Инициализация БД
 */

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRoleService userRoleService;
    private final FirestoreService firestoreService;
    private final BaseDictionaryService baseDictionaryService;

    private final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    public DatabaseInitializer(UserRoleService userRoleService, FirestoreService firestoreService, BaseDictionaryService baseDictionaryService) {
        this.userRoleService = userRoleService;
        this.firestoreService = firestoreService;
        this.baseDictionaryService = baseDictionaryService;
    }

    @Override
    public void run(String @NonNull ... args) throws ExecutionException, InterruptedException {
        // создание базовых ролей
        initUserRoles();
        //назначем роль User всем к кого ее нет
        assignDefaultRoleToAllUsers();
        //добавление ЗЗ, тут error
        initZodiacSigns();
        //инициалищация соционики
        initSocionics();
        //инициализация МБТИ
        initMbti();
        //инициализация тегов
        //инициализация тествых вселенных(временный код, пока нормально не сделаю)
        initUniverses();
        //инициализация тестовых персонажей(временный код)
        initCrushes();
    }

    /**
     * Инициализация пользовательских ролей
     */
    private void initUserRoles() throws ExecutionException, InterruptedException {
        //дефольные роли
        String[][] defaultRoles = {
                {"USER", "Обычный пользователь с базовыми правами"},
                {"ADMIN", "Полный доступ ко всем функциям"},
        };

        for (String[] roleData : defaultRoles) {
            String roleName = roleData[0];
            String roleDescription = roleData[1];
            // Проверяем, существует ли уже такая роль
            UserRole existingRole = userRoleService.findByName(roleName);
            if (existingRole == null) {
                // Роль не существует — создаём
                UserRole newRole = new UserRole();
                newRole.setName(roleName);
                newRole.setDescription(roleDescription);
                userRoleService.save(newRole);
            }
        }
    }


    /**
     * Назначить роль USER всем пользователям, у которых её нет
     */
    private void assignDefaultRoleToAllUsers() throws ExecutionException, InterruptedException {
        // Получаем роль USER
        UserRole userRole = userRoleService.findByName("USER");
        if (userRole == null) {
            return;
        }
        // Получаем ID роли
        String userRoleId = userRoleService.getIdByName("USER");
        if (userRoleId == null) {
            return;
        }
        List<User> allUsers = firestoreService.findAllUsers();
        if (allUsers.isEmpty()) {
            return;
        }

        for (User user : allUsers) {
            List<String> roleIds = user.getRoleIds();
            // Если у пользователя нет ролей или нетUSER
            if (roleIds == null || !roleIds.contains(userRoleId)) {
                // Добавляем роль USER
                if (roleIds == null) {
                    roleIds = new ArrayList<>();
                }
                roleIds.add(userRoleId);
                user.setRoleIds(roleIds);
                // Сохраняем пользователя
                firestoreService.saveUser(user);
            }
        }
    }
    /**
     * Инициализация ЗЗ
     */
    private void initZodiacSigns() throws ExecutionException, InterruptedException {
        log.info("\nПроверка знаков зодиака");
        String[] zodiacData = {
                "Овен",
                "Телец",
                "Близнецы",
                "Рак",
                "Лев",
                "Дева",
                "Весы",
                "Скорпион",
                "Стрелец",
                "Козерог",
               "Водолей",
                "Рыбы"
        };
        int createdCount = 0;
        int existingCount = 0;

        for (String data : zodiacData) {
            ZodiacSign existing = baseDictionaryService.findByField(
                   "zodiac_signs", ZodiacSign.class, "name", data
            );

            if (existing == null) {
                ZodiacSign sign = new ZodiacSign(data);
                baseDictionaryService.save("zodiac_signs", sign);
                log.info("создан знак зодиака: {}", data);
                createdCount++;
            }
        }
        log.info("Создано новых: {}", createdCount);
        log.info("Обновлено существующих: {}", existingCount);
    }

    /**
     * Инициализация Соционики
     */
    private void initSocionics() throws ExecutionException, InterruptedException {
        log.info("\nПроверка типов соционики...");
        String[][] socionicsData = {
                // shortId, fullName, shortName, description
                {"ILE", "Интуитивно-логический экстраверт", "Дон Кихот",
                        "Генератор безумных идей, который постоянно ищет новые возможности и скрытый потенциал в окружающем мире. Ему тяжело дается рутина, поэтому он легко переключается с одного незаконченного проекта на другой. Обладает детским любопытством и часто игнорирует общепринятые рамки и авторитеты."},

                {"SEI", "Сенсорно-этический интроверт", "Дюма",
                        "Праздный, избалованный роскошью человек, который живет в удовольствиях и избегает любых жизненных трудностей. Мастерски создает вокруг себя атмосферу уюта, тепла и гастрономического удовольствия. Он тонко чувствует физическое состояние окружающих и искренне стремится окружить их заботой. Избегает любых конфликтов, предпочитая решать споры мирным путем и сохранять душевный покой."},

                {"ESE", "Этико-сенсорный экстраверт", "Гюго",
                        "Человек-праздник с неиссякаемым запасом бурных эмоций, который заряжает своим позитивом любую компанию. Он не выносит уныния и медлительности, поэтому сразу берет организацию веселья и быта в свои руки. Гостеприимен, заботлив, но бывает излишне суетлив в попытках сделать всем хорошо."},

                {"LII", "Логико-интуитивный интроверт", "Робеспьер",
                        "Обладает безупречным аналитическим умом, стремясь разложить любое явление на четкие законы и логические схемы. Для него критически важны справедливость, объективность и равенство прав для всех людей. Живет в мире интеллектуальных концепций, из-за чего со стороны может казаться холодным или отстраненным."},

                {"EIE", "Этико-интуитивный экстраверт", "Гамлет",
                        "Яркий режиссер человеческих эмоций, который видит мир как глобальную театральную драму и предчувствует грядущие перемены. Он умеет зажигать идеи в сердцах тысяч людей и вести их за собой ради великой цели. При этом в повседневном быту часто чувствует себя беспомощным и нуждается в надежной опоре."},

                {"LSI", "Логико-сенсорный интроверт", "Максим Горький",
                        "Человек железной дисциплины, для которого порядок, инструкции и субординация превыше всего. Он создает стабильные структуры, тщательно контролирует их выполнение и требует от окружающих абсолютной исполнительности. На него всегда можно положиться в кризисной ситуации, так как его разум остается холодным."},

                {"SLE", "Сенсорно-логический экстраверт", "Жуков",
                        "Волевой и решительный лидер, воспринимающий жизнь как поле боя, где обязательно нужно одержать победу. Он мгновенно оценивает расстановку сил, захватывает пространство и эффективно управляет любыми ресурсами. Не терпит слабости и сомнений, действуя жестко, прямолинейно и всегда на результат."},

                {"IEI", "Интуитивно-этический интроверт", "Есенин",
                        "Романтичный мечтатель с развитой интуицией, который тонко чувствует эмоциональное состояние людей и умеет выжидать нужный момент. Он сглаживает острые углы в коллективе своим мягким юмором и вдохновляет окружающих верой в светлое будущее. Подсознательно ищет сильного покровителя, способного защитить его от суровой реальности."},

                {"SEE", "Сенсорно-этический экстраверт", "Наполеон",
                        "Обаятельный и уверенный в себе лидер, который мастерски манипулирует отношениями ради достижения высокого статуса и власти. Он мгновенно завоевывает симпатии, расширяет сферу своего влияния и умеет добиваться желаемого от людей. Руководствуется личными амбициями и не боится открытой конкуренции."},

                {"ILI", "Интуитивно-логический интроверт", "Бальзак",
                        "Мудрый философ и глубокий скептик, который видит скрытые риски и безошибочно прогнозирует финал любого начинания. Он крайне экономно расходует свою энергию, из-за чего часто кажется ленивым или созерцательным. Обладает тонким ироничным юмором и помогает охлаждать излишний пыл излишне суетливых коллег."},

                {"LIE", "Логико-интуитивный экстраверт", "Джек Лондон",
                        "Динамичный предприниматель, который постоянно ищет выгоду, пользу и новые эффективные методы работы. Он обожает оправданный риск, спорт, путешествия и готов работать круглые сутки ради масштабного бизнес-проекта. Совершенно не заботится о собственном комфорте и внешнем виде, фокусируясь только на результате."},

                {"ESI", "Этико-сенсорный интроверт", "Драйзер",
                        "Хранитель моральных устоев, который мгновенно считывает фальшь в отношениях и строго делит людей на преданных «своих» и опасных «чужих». Он молчаливо и упорно защищает границы своей семьи, требуя от близких верности и выполнения долга. Обладает твердым характером и не прощает предательства."},

                {"LSE", "Логико-сенсорный экстраверт", "Штирлиц",
                        "Настоящий профессионал и эталонный администратор, который организует любой трудовой процесс с максимальным качеством и пользой. Он ценит честный труд, проверенные факты и проявляет трогательную заботу о материальном комфорте окружающих. Работает на износ и требует от других такой же самоотдачи."},

                {"EII", "Этико-интуитивный интроверт", "Достоевский",
                        "Главный гуманист соционического мира, обладающий безграничной эмпатией, добротой и врожденным пониманием чужой психологии. Он тонко чувствует любые проявления несправедливости, избегает насилия и стремится примирить враждующие стороны. Является «совестью» своего окружения, тихо и ненавязчиво помогая людям стать лучше."},

                {"IEE", "Интуитивно-этический экстраверт", "Гексли",
                        "Проницательный советчик, который с первого взгляда видит скрытые таланты человека и вдохновляет его на развитие. Он обожает заводить новые знакомства, легко очаровывает людей и виртуозно уходит от скучных обязательств. Живет в мире увлекательных перспектив и ярких человеческих взаимоотношений."},

                {"SLI", "Сенсорно-логический интроверт", "Габен",
                        "Природный изобретатель и мастер на все руки, стремящийся организовать свою жизнь с минимальными затратами сил и максимальным удобством. Он обладает безупречным эстетическим вкусом, ценит качественные вещи и надежность в работе. Скрытен в эмоциях, но всегда готов делом помочь тем, кого действительно ценит."}
        };

        int createdCount = 0;
        int existingCount = 0;

        for (String[] data : socionicsData) {
            String shortId = data[0];
            String fullName = data[1];
            String shortName = data[2];
            String description = data[3];

            Socionics existing =  baseDictionaryService.findByField(
                    "socionics", Socionics.class, "shortId", shortId
            );

            if (existing == null) {
                Socionics socionics = new Socionics(shortId, fullName, shortName, description);
                baseDictionaryService.save("socionics", socionics);
                createdCount++;
            }
        }
        log.info("Создано новых записей тип Socionics: {}", createdCount);
        log.info("Обновлено существующих тип Socionics: {}", existingCount);
    }

    /**
     * Инициализация типов MBTI
     */
    private void initMbti() throws ExecutionException, InterruptedException {
        System.out.println("\n🧪 ПРОВЕРКА ТИПОВ MBTI...");

        String[][] mbtiData = {
                {"INTJ", "Стратег (Architect)",
                        "Обладают стратегическим и новаторским мышлением. У них есть план на всё, и они решительно воплощают свои идеи в жизнь. Независимые, логичные и амбициозные, всегда видят общую картину."},

                {"INTP", "Учёный (Logician)",
                        "Неутолимые изобретатели идей с неутолимой жаждой знаний. Они живут в мире теоретических возможностей, обожают находить логические несостыковки и создавать сложные системы."},

                {"ENTJ", "Командир (Commander)",
                        "Прирождённые лидеры с железной волей и харизмой. Они видят неэффективность и с лёгкостью берут на себя управление, чтобы всё исправить. Решительные, смелые и нацеленные на результат."},

                {"ENTP", "Полемист (Debater)",
                        "Умные и любопытные мыслители, которые обожают интеллектуальные споры. Они любят рассматривать проблему со всех сторон, играя роль «адвоката дьявола», и не боятся бросать вызов общепринятым нормам."},

                {"INFJ", "Активист (Advocate)",
                        "Тихие и загадочные идеалисты с очень сильными принципами. Они глубоко понимают человеческую натуру и решительно борются за то, во что верят, вдохновляя других своим примером."},

                {"INFP", "Посредник (Mediator)",
                        "Поэтичные, добрые и альтруистичные натуры с богатым внутренним миром. Они всегда ищут добро в людях и ситуациях, руководствуясь своими глубокими ценностями и стремлением к гармонии."},

                {"ENFJ", "Тренер (Protagonist)",
                        "Харизматичные и вдохновляющие лидеры, которые искренне верят в людей. Они обладают даром понимать и мотивировать других, помогая им раскрыть свой потенциал."},

                {"ENFP", "Борец (Campaigner)",
                        "Творческие, общительные и свободолюбивые энтузиасты. Они видят жизнь как одно большое приключение, полное возможностей, и с лёгкостью устанавливают глубокие эмоциональные связи с другими."},

                {"ISTJ", "Администратор (Logistician)",
                        "Воплощение надёжности и ответственности. Они практичны, опираются на факты и преданы своему долгу. Их невозможно упрекнуть в лени или нечестности."},

                {"ISFJ", "Защитник (Defender)",
                        "Очень преданные и тёплые защитники, всегда готовые прийти на помощь своим близким. Они скромны, трудолюбивы и находят радость в заботе о других."},

                {"ESTJ", "Менеджер (Executive)",
                        "Превосходные организаторы, которые ценят порядок и традиции. Они отлично управляют людьми и процессами, следуя чётким правилам и принципам. Прямолинейны и честны."},

                {"ESFJ", "Консул (Consul)",
                        "Чрезвычайно общительные и заботливые люди, «душа компании». Они всегда готовы помочь, поддержать и организовать любое социальное мероприятие, стремясь к гармонии в коллективе."},

                {"ISTP", "Виртуоз (Virtuoso)",
                        "Смелые и практичные экспериментаторы, которые обожают разбираться, как всё устроено. Они отлично работают руками и умом, с лёгкостью решая любые практические задачи."},

                {"ISFP", "Артист (Adventurer)",
                        "Обаятельные и гибкие художники, всегда готовые к новому опыту. Они живут настоящим моментом, исследуя мир через свою эстетику и чувства. Ценят свободу и самовыражение."},

                {"ESTP", "Делец (Entrepreneur)",
                        "Энергичные, умные и очень проницательные люди, которые любят быть в центре событий. Они обожают риск и действие, с лёгкостью находя выход из любой сложной ситуации."},

                {"ESFP", "Развлекатель (Entertainer)",
                        "Спонтанные, энергичные и весёлые артисты, которые не могут жить без внимания. Их жизнь — это сцена, и они с радостью делятся своим оптимизмом и жизнелюбием с окружающими."}
        };

        int createdCount = 0;
        int existingCount = 0;

        for (String[] data : mbtiData) {
            String code = data[0];
            String fullName = data[1];
            String description = data[2];

            MBTI existing = baseDictionaryService.findByField(
                    "mbti", MBTI.class, "shortName", code);

            if (existing == null) {
                MBTI mbti = new MBTI(fullName, code, description);
                baseDictionaryService.save("mbti", mbti);
                createdCount++;
            }
        }
        log.info("Создано новых записей тип MBTI: {}", createdCount);
        log.info("Обновлено существующих тип MBTI: {}", existingCount);
    }

    /**
     * Инициализация тегов
     */
    private void initTags() throws ExecutionException, InterruptedException {

        String[][] tagData = {
                // ВНЕШНОСТЬ
                {"Очкарик", "APPEARANCE", "Персонаж носит очки"},
                {"Гетерохромия", "APPEARANCE", "Разный цвет глаз"},
                {"Ёжик", "APPEARANCE", "Причёска ёжик"},
                {"Кудрявый", "APPEARANCE", "Кудрявые волосы"},
                {"Длинные волосы", "APPEARANCE", "Длинные волосы"},
                {"Лысый", "APPEARANCE", "Отсутствие волос"},
                {"Родинка", "APPEARANCE", "Родинка на теле или лице"},
                {"Пирсинг", "APPEARANCE", "Пирсинг или серьги"},
                {"Ушастый", "APPEARANCE", "Звериные уши и/или хвост"},
                {"Животное", "APPEARANCE", "Выраженные черты животного или само животное"},
                {"Прищур", "APPEARANCE", "Характерный прищур глаз"},
                {"Шапка", "APPEARANCE", "В шапке или другом головном уборе"},
                {"Шкаф", "APPEARANCE", "Очень крупное телосложение"},
                {"Тату", "APPEARANCE", "Татуировки или рисунки на теле"},
                {"Каре", "APPEARANCE", "Каре или волосы до плеч"},
                {"Косички", "APPEARANCE", "Косички в образе"},

                //  ПОЗИЦИЯ В СЮЖЕТЕ
                {"ГГ", "POSITION_IN_THE_PLOT", "Главный герой"},
                {"Злодей", "POSITION_IN_THE_PLOT", "Злодей или антагонист"},
                {"Антигерой", "POSITION_IN_THE_PLOT", "Менял сторону по сюжету, неоднозначный персонаж"},

                //  РАСА
                {"Демон", "RACE", "Демон"},
                {"Оборотень", "RACE", "Оборотень"},
                {"Вампир", "RACE", "Вампир"},
                {"Святой", "RACE", "Бог, ангел или проповедник во вселенной"},
                {"Эльф", "RACE", "Эльф"},
                {"Киборг", "RACE", "Киборг или робот"},
                {"Королевские корни", "RACE", "Королевского происхождения"},

                // ДЕЯТЕЛЬНОСТЬ
                {"Творец", "ACTIVITY", "Художник, писатель и т.д."},
                {"Певец", "ACTIVITY", "Певец или музыкант"},
                {"Доктор", "ACTIVITY", "Врач или доктор"},
                {"Учёный", "ACTIVITY", "Учёный, исследователь"},
                {"Командир", "ACTIVITY", "Командовал отрядом или компанией"},
                {"Хулиган", "ACTIVITY", "Малолетние дебилы или состояли в школьных группировках"},
                {"Учитель", "ACTIVITY", "Учитель или наставник"},

                // ОРУЖИЕ
                {"Мечник", "WEAPON", "Использовал меч как основное оружие"},
                {"Стрелок", "WEAPON", "Использовал лук, пистолеты или пушки"},
                {"Кулаки", "WEAPON", "Использовал рукопашный бой или знает его очень хорошо"},

                // ДРУГОЕ
                {"Спортивный", "OTHER", "Использовал меч как основное оружие"},
                {"Музыкант", "OTHER", "Играл на каком-либо музыкальном предмете во время сюжета"},
                {"Скрепы", "OTHER", "Всё, что в стране не принимается"},
                {"Курильщик", "OTHER", ""},
                {"Пьяница", "OTHER", ""},
                {"Хозяйственный", "OTHER", ""},
                {"Псих", "OTHER", ""},
        };

        int createdCount = 0;
        int existingCount = 0;

        for (String[] data : tagData) {
            String name = data[0];
            String groupCode = data[1];
            String description = data[2];

            Tag existing = baseDictionaryService.findByField(
                    "tag", Tag.class, "name", name
            );

            if (existing == null) {
                TagGroup group = TagGroup.fromCode(groupCode);
                Tag tag = new Tag(name, group, description);
                baseDictionaryService.save("tag",tag);
                createdCount++;
            }
        }
        log.info("Создано новых записей тип Tag: {}", createdCount);
        log.info("Обновлено существующих тип Tag: {}", existingCount);
    }

    /**
     * Инициализация жанров
     */
    private void initGenres() throws ExecutionException, InterruptedException {
        //добавлено с описанием, хотя пока его не будем использовать или дальше поменяем
        String[][] genreData = {
                {"Комедия", "Юмористические, смешные произведения"},
                {"Драма", "Серьёзные, эмоциональные произведения"},
                {"Трагедия", "Произведения с печальным финалом"},
                {"Романтика", "Любовные истории, романтические линии"},
                {"Китайское", "Произведения из Китая (манхуа, дорамы, фильмы)"},
                {"Манхва", "Корейские комиксы"},
                {"Аниме", "Японская анимация"},
                {"Корейское", "Произведения из Кореи (дорамы, фильмы)"},
                {"Ужасы", "Пугающие, страшные произведения"},
                {"Боевик", "Экшн, перестрелки, погони"},
                {"Экшен", "Динамичные сцены, драки"},
                {"Фантастика", "Научная фантастика, будущее, технологии"},
                {"Музыкальное", "Произведения с музыкой, мюзиклы"},
                {"Игра", "Видеоигры, игровые вселенные"},
                {"Сёнэн", "Для мальчиков (японская классификация)"},
                {"Сёдзё", "Для девочек (японская классификация)"},
                {"Сэйнэн", "Для взрослых мужчин (японская классификация)"},
                {"Дзёсэй", "Для взрослых женщин (японская классификация)"},
                {"Фильм", "Полнометражное кино"},
                {"Сериал", "Многосерийные произведения"},
                {"Мультфильм", "Анимационные произведения"},
                {"Книга", "Литературные произведения"}
        };

        int createdCount = 0;
        int existingCount = 0;

        for (String[] data : genreData) {
            String name = data[0];
            String description = data[1];

            Genre existing =  baseDictionaryService.findByField(
                    "genre", Genre.class, "name", name
            );

            if (existing == null) {
                Genre genre = new Genre(name, description);
                baseDictionaryService.save("genre", genre);
                createdCount++;
            } else {
                existingCount++;
            }
        }
        log.info("Создано новых записей тип Genre: {}", createdCount);
        log.info("Обновлено существующих тип Genre: {}", existingCount);
    }


    /**
     * Инициализация тестовых вселенных
     */
    private void initUniverses() throws ExecutionException, InterruptedException {
        List<Genre> allGenres = baseDictionaryService.findAll("genre", Genre.class);
        Map<String, String> genreMap = new HashMap<>();
        for (Genre genre : allGenres) {
            genreMap.put(genre.getName(), genre.getId());
        }
        String[][] universeData = {
                {"Марвел", "Вселенная Marvel Comics — супергерои, мутанты и космические приключения.",
                        "Боевик,Экшен,Фантастика,Фильм,Сериал,Мультфильм"},
                {"DC", "Вселенная DC Comics — легендарные герои и злодеи.",
                        "Боевик,Экшен,Фантастика,Фильм,Сериал,Мультфильм"},
                {"Звёздные войны", "Давным-давно в далёкой-далёкой галактике...",
                        "Фантастика,Боевик,Экшен,Фильм,Сериал,Мультфильм"},
                {"Властелин колец", "Средиземье — мир эльфов, гномов и хоббитов.",
                        "Фэнтези,Драма,Приключения,Фильм,Книга"},
                {"Гарри Поттер", "Мир магии, Хогвартса и волшебников.",
                        "Фэнтези,Приключения,Драма,Фильм,Книга"},
                {"Игра престолов", "Семь королевств Вестероса — борьба за Железный трон.",
                        "Фэнтези,Драма,Сериал,Книга"},
                {"Аниме-вселенная", "Сборная вселенная популярных аниме-миров.",
                        "аниме,Фантастика,Экшен,Сёнэн,Мультфильм"},
                {"Киберпанк", "Мир высоких технологий и мрачного будущего.",
                        "Фантастика,Киберпанк,Экшен,Игра,Фильм,Сериал"},
                {"Дисней", "Мир волшебных сказок и приключений от Disney.",
                        "Мультфильм,Приключения,Семейный,Фильм"},
                {"Оригинальная вселенная", "Уникальный мир, созданный для CrushVerse.",
                        "Фэнтези,Приключения,Романтика,Драма"}
        };
        for (String[] data : universeData) {
            String name = data[0];
            String description = data[1];
            String genreNamesStr = data[2];
            String[] genreNames = genreNamesStr.split(",");
            List<String> genreIds = new ArrayList<>();
            for (String genreName : genreNames) {
                String genreId = genreMap.get(genreName.trim());
                if (genreId != null) {
                    genreIds.add(genreId);
                }
            }
            Universe existing = baseDictionaryService.findByField("universe", Universe.class, "name", name);
            if (existing == null) {
                Universe universe = new Universe(name, description, genreIds);
                baseDictionaryService.save("universe", universe);
            }
        }
    }

    /**
     * Инициализация крашей с рандомными связями(тестовые данные, дальше поправяим)
     */
    private void initCrushes() throws ExecutionException, InterruptedException {
        // Получаем все данные из справочников
        List<Universe> universes = baseDictionaryService.findAll("universe", Universe.class);
        List<ZodiacSign> zodiacs = baseDictionaryService.findAll("zodiac_sign", ZodiacSign.class);
        List<Socionics> socionics = baseDictionaryService.findAll("socionics", Socionics.class);
        List<MBTI> mbtiList = baseDictionaryService.findAll("mbti", MBTI.class);
        List<Tag> tags = baseDictionaryService.findAll("tag", Tag.class);

        // Создаём списки ID для случайного выбора
        List<String> universeIds = universes.stream().map(Universe::getId).toList();
        List<String> zodiacIds = zodiacs.stream().map(ZodiacSign::getId).toList();
        List<String> socionicsIds = socionics.stream().map(Socionics::getId).toList();
        List<String> mbtiIds = mbtiList.stream().map(MBTI::getId).toList();
        List<String> tagIds = tags.stream().map(Tag::getId).toList();
        Random random = new Random();
        String[][] crushData = {
                {"Арагорн", "Настоящий король, объединяющий народы Средиземья.", "198", "3018"},
                {"Леголас", "Эльфийский принц, мастер стрельбы из лука.", "183", "87"},
                {"Гимли", "Отважный гном, верный друг.", "152", "252"},
                {"Фродо", "Хранитель Кольца, несущий бремя судьбы мира.", "135", "2968"},
                {"Гэндальф", "Мудрый маг, наставник и защитник.", "185", "1000"},
                {"Арвен", "Эльфийская принцесса, возлюбленная Арагорна.", "170", "2777"},
                {"Эовин", "Белая леди Рохана, воительница.", "168", "2995"},
                {"Саурон", "Тёмный властелин, создатель Кольца Всевластья.", "280", "1000"},

                {"Гарри Поттер", "Мальчик, который выжил, и великий волшебник.", "178", "1980"},
                {"Гермиона Грейнджер", "Самый умный ведьма своего поколения.", "165", "1979"},
                {"Рон Уизли", "Верный друг, член семьи Уизли.", "183", "1980"},
                {"Дамблдор", "Величайший директор Хогвартса.", "190", "1881"},
                {"Северус Снейп", "Загадочный профессор с тёмным прошлым.", "183", "1960"},

                {"Тони Старк", "Железный Человек, гениальный изобретатель и плейбой.", "185", "1970"},
                {"Стив Роджерс", "Капитан Америка, символ свободы.", "188", "1918"},
                {"Наташа Романофф", "Чёрная Вдова, мастер боевых искусств.", "165", "1984"},
                {"Тор", "Бог грома, наследник Асгарда.", "198", "1983"},
                {"Локи", "Бог хитрости и обмана.", "193", "1982"},

                {"Люк Скайуокер", "Последний джедай, приносящий надежду галактике.", "185", "1981"},
                {"Дарт Вейдер", "Тёмный лорд ситхов, отец Люка.", "200", "1977"},
                {"Принцесса Лея", "Лидер Повстанцев, принцесса Альдераана.", "155", "1983"},
                {"Хан Соло", "Контрабандист, капитан Тысячелетнего сокола.", "180", "1977"},

                {"Шерлок Холмс", "Величайший детектив всех времён.", "183", "1970"},
                {"Доктор Стрэндж", "Верховный маг, защитник мира.", "188", "1970"}
        };
        for (String[] data : crushData) {
            String name = data[0];
            String description = data[1];
            int height = Integer.parseInt(data[2]);
            int dateOfBirth = Integer.parseInt(data[3]);
            String randomUniverseId = universeIds.isEmpty() ? null : universeIds.get(random.nextInt(universeIds.size()));
            String randomZodiacId = zodiacIds.isEmpty() ? null : zodiacIds.get(random.nextInt(zodiacIds.size()));
            String randomSocionicsId = socionicsIds.isEmpty() ? null : socionicsIds.get(random.nextInt(socionicsIds.size()));
            String randomMbtiId = mbtiIds.isEmpty() ? null : mbtiIds.get(random.nextInt(mbtiIds.size()));
            List<String> randomTagIds = new ArrayList<>();
            if (!tagIds.isEmpty()) {
                int tagCount = 2 + random.nextInt(4); // 2-5 тегов
                List<String> shuffledTags = new ArrayList<>(tagIds);
                Collections.shuffle(shuffledTags);
                randomTagIds = shuffledTags.subList(0, Math.min(tagCount, shuffledTags.size()));
            }
            Crush existing = baseDictionaryService.findByField(
                    "crush", Crush.class, "name", name
            );
            if (existing == null) {
                Crush crush = new Crush();
                crush.setName(name);
                crush.setDescription(description);
                crush.setUniverseId(randomUniverseId);
                crush.setZodiacSignId(randomZodiacId);
                crush.setSocionicsId(randomSocionicsId);
                crush.setMbtiId(randomMbtiId);
                crush.setHeight(height);
                crush.setDateOfBirth(dateOfBirth);
                crush.setTegsIds(randomTagIds);
                baseDictionaryService.save("crush", crush);
                // Получаем название вселенной для вывода
                String universeName = universes.stream()
                        .filter(u -> u.getId().equals(randomUniverseId))
                        .map(Universe::getName)
                        .findFirst()
                        .orElse("Без вселенной");

            }
        }
    }

}