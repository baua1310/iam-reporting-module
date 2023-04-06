package de.uniregensburg.iamreportingmodule.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import de.uniregensburg.iamreportingmodule.core.service.JobSchedulingService;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import de.uniregensburg.iamreportingmodule.data.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Generator of demo data for local development and testing
 *
 * @author Julian Bauer
 */
@SpringComponent
@Profile("!production")
public class DataGenerator {
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Generates demo data
     *
     * @param userRepository
     * @param encoder
     * @param stakeholderRepository
     * @param audienceRepository
     * @param informationNeedRepository
     * @param manualDataSourceRepository
     * @param databaseDataSourceRepository
     * @param fileDataSourceRepository
     * @param measurementRepository
     * @param jobSchedulingService
     * @param metricRepository
     * @return
     */
    @Bean CommandLineRunner generateData(UserRepository userRepository,
                                         PasswordEncoder encoder,
                                         StakeholderRepository stakeholderRepository,
                                         AudienceRepository audienceRepository,
                                         InformationNeedRepository informationNeedRepository,
                                         ManualDataSourceRepository manualDataSourceRepository,
                                         DatabaseDataSourceRepository databaseDataSourceRepository,
                                         FileDataSourceRepository fileDataSourceRepository,
                                         MeasurementRepository measurementRepository,
                                         JobSchedulingService jobSchedulingService,
                                         MetricRepository metricRepository) {

        return  args -> {

            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating database entries");

            logger.info("Generating users");

            logger.info("Adding admin user");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("password"));
            admin.setFirstName("Application");
            admin.setLastName("Administrator");
            admin.setAdmin(true);

            logger.info("Adding default user");
            User user = new User();
            user.setAdmin(false);
            user.setUsername("user");
            user.setPassword(encoder.encode("password"));
            user.setFirstName("Application");
            user.setLastName("User");

            logger.info("Saving users");
            userRepository.saveAll(Set.of(admin, user));

            logger.info("Generating stakeholders");

            logger.info("CIO - members: user");
            Stakeholder cio = new Stakeholder();
            cio.setName("CIO");
            cio.setMembers(Set.of(user));

            logger.info("CISO - members: admin");
            Stakeholder ciso = new Stakeholder();
            ciso.setName("CISO");
            ciso.setMembers(Set.of(admin));

            logger.info("Saving stakeholders");
            stakeholderRepository.saveAll(Set.of(cio, ciso));

            logger.info("Generating audiences");

            logger.info("C-Level - members: admin, user");
            Audience cLevel = new Audience();
            cLevel.setName("C-Level");
            cLevel.setMembers(Set.of(admin, user));

            logger.info("Manager - members:");
            Audience manager = new Audience();
            manager.setName("Manager");

            logger.info("Saving audiences");
            audienceRepository.saveAll(Set.of(cLevel, manager));

            logger.info("Generating information needs");

            logger.info("Risk reduction");
            InformationNeed riskReduction = new InformationNeed();
            riskReduction.setName("Risk reduction");
            riskReduction.setDescription("Reduce risk");

            logger.info("Improvement of process and data quality");
            InformationNeed improvementOfProcessAndDataQuality = new InformationNeed();
            improvementOfProcessAndDataQuality.setName("Improvement of process and data quality");
            improvementOfProcessAndDataQuality.setDescription("Improve process and data quality");

            logger.info("Regulatory compliance");
            InformationNeed regulatoryCompliance = new InformationNeed();
            regulatoryCompliance.setName("Regulatory compliance");
            regulatoryCompliance.setDescription("Comply with the legal requirements");

            logger.info("Business faciliatation");
            InformationNeed businessFacilitation = new InformationNeed();
            businessFacilitation.setName("Business facilitation");
            businessFacilitation.setDescription("Facilitate business");

            logger.info("Saving information needs");
            informationNeedRepository.saveAll(Set.of(riskReduction, improvementOfProcessAndDataQuality,
                    regulatoryCompliance, businessFacilitation));

            logger.info("Generating manual datasources");

            logger.info("Paswords");
            ManualDataSource manualDataSourcePasswords = new ManualDataSource();
            manualDataSourcePasswords.setName("Passwords");
            manualDataSourcePasswords.setDescription("Total number of all passwords");
            manualDataSourcePasswords.setValue(BigDecimal.valueOf(1000));

            logger.info("Leaked passwords");
            ManualDataSource manualDataSourceLeakedPasswords = new ManualDataSource();
            manualDataSourceLeakedPasswords.setName("Leaked passwords");
            manualDataSourceLeakedPasswords.setDescription("Total number of leaked passwords");
            manualDataSourceLeakedPasswords.setValue(BigDecimal.valueOf(4));

            logger.info("Weak passwords");
            ManualDataSource manualDataSourceWeakPasswords = new ManualDataSource();
            manualDataSourceWeakPasswords.setName("Weak passwords");
            manualDataSourceWeakPasswords.setDescription("Total number of weak passwords");
            manualDataSourceWeakPasswords.setValue(BigDecimal.valueOf(115));

            logger.info("Identical passwords");
            ManualDataSource manualDataSourceIdenticalPasswords = new ManualDataSource();
            manualDataSourceIdenticalPasswords.setName("Identical passwords");
            manualDataSourceIdenticalPasswords.setDescription("Total number of Identical passwords");
            manualDataSourceIdenticalPasswords.setValue(BigDecimal.valueOf(23));

            logger.info("Average password length");
            ManualDataSource manualDataSourcePasswordLength = new ManualDataSource();
            manualDataSourcePasswordLength.setName("Average password length");
            manualDataSourcePasswordLength.setDescription("Average length of passwords");
            manualDataSourcePasswordLength.setValue(BigDecimal.valueOf(9.3));

            logger.info("Average password age");
            ManualDataSource manualDataSourcePasswordAge = new ManualDataSource();
            manualDataSourcePasswordAge.setName("Average password age");
            manualDataSourcePasswordAge.setDescription("Average age of passwords in days");
            manualDataSourcePasswordAge.setValue(BigDecimal.valueOf(983));

            logger.info("Saving manual datasources");
            manualDataSourceRepository.saveAll(Set.of(manualDataSourcePasswords,
                    manualDataSourceLeakedPasswords,
                    manualDataSourceWeakPasswords,
                    manualDataSourceIdenticalPasswords,
                    manualDataSourcePasswordLength,
                    manualDataSourcePasswordAge));

            logger.info("Generating database datasources");

            logger.info("Test database: postgresql://localhost:5432/test - user: test - password: Test123!");
            DatabaseDataSource databaseDataSourceTest = new DatabaseDataSource();
            databaseDataSourceTest.setName("Test database");
            databaseDataSourceTest.setDescription("PostgreSQL localhost");
            databaseDataSourceTest.setHost("localhost");
            databaseDataSourceTest.setPort(5432);
            databaseDataSourceTest.setDatabase("test");
            databaseDataSourceTest.setUsername("test");
            databaseDataSourceTest.setPassword("Test123!");
            databaseDataSourceTest.setDbmsType(Dbms.POSTGRESQL);

            logger.info("Saving database datasources");
            databaseDataSourceRepository.saveAll(Set.of(databaseDataSourceTest));

            logger.info("Generating file datasources");

            logger.info("CSV datasource - useraccounts.csv");
            FileDataSource csvfileDataSource = new FileDataSource();
            csvfileDataSource.setName("User accounts");
            csvfileDataSource.setDescription("CSV file datasource");
            csvfileDataSource.setFileType(FileType.CSV);
            csvfileDataSource.setFile(Base64.getDecoder().decode("Vm9ybmFtZTtOYWNobmFtZTtBbHRlcg0KQW5uZTtNYXVlcjszMA0KTWFyY287TWV5ZXI7MjMNCkthdHJpbjtGaW5rOzU0DQpCcmlnaXR0ZTtGbGVpc2NoZXI7MzQNCkRvbWluaWs7SG9sem1hbm47MjANCkFubmE7V2ViZXI7MzQNCg=="));
            csvfileDataSource.setFileName("useraccounts.csv");

            logger.info("Saving file datasources");
            fileDataSourceRepository.saveAll(Set.of(csvfileDataSource));

            logger.info("Generating measurements");

            // average age (csv)
            logger.info("Average age");
            Measurement averageAge = new Measurement();
            averageAge.setDataSource(csvfileDataSource);
            Map<String, String> averageAgeAttributes = new HashMap<>();
            averageAgeAttributes.put("csvHeader", "true");
            averageAgeAttributes.put("csvColumnName", "Alter");
            averageAgeAttributes.put("csvAggregationMethod", CsvAggregationMethod.AVERAGE.toString());
            averageAgeAttributes.put("csvDelimiter", ";");
            averageAge.setAttributes(averageAgeAttributes);
            averageAge.setName("Average age");
            averageAge.setLabel("averageAge");
            averageAge.setDescription("Average age of users");
            averageAge.setAudiences(Set.of(cLevel, manager));
            averageAge.setInformationNeeds(Set.of(businessFacilitation));
            averageAge.setStakeholders(Set.of(ciso));
            averageAge.setScale(Scale.RATIO);
            averageAge.setUnit(Unit.TOTAL);
            averageAge.setFrequency(new Frequency(Duration.parse("PT1M")));

            // maximum age (csv)
            logger.info("Maximum age");
            Measurement maximumAge = new Measurement();
            maximumAge.setDataSource(csvfileDataSource);
            Map<String, String> maximumAgeAttributes = new HashMap<>();
            maximumAgeAttributes.put("csvHeader", "true");
            maximumAgeAttributes.put("csvColumnName", "Alter");
            maximumAgeAttributes.put("csvAggregationMethod", CsvAggregationMethod.MAXIMUM.toString());
            maximumAgeAttributes.put("csvDelimiter", ";");
            maximumAge.setAttributes(maximumAgeAttributes);
            maximumAge.setName("Maximum age");
            maximumAge.setLabel("maximumAge");
            maximumAge.setDescription("Maximum age of users");
            maximumAge.setAudiences(Set.of(cLevel, manager));
            maximumAge.setInformationNeeds(Set.of(businessFacilitation));
            maximumAge.setStakeholders(Set.of(ciso));
            maximumAge.setScale(Scale.RATIO);
            maximumAge.setUnit(Unit.TOTAL);
            maximumAge.setFrequency(new Frequency(Duration.parse("PT5M")));
            
            // total accounts (csv)
            logger.info("total accounts (csv)");
            Measurement totalAccountsCsv = new Measurement();
            totalAccountsCsv.setDataSource(csvfileDataSource);
            Map<String, String> totalAccountsCsvAttributes = new HashMap<>();
            totalAccountsCsvAttributes.put("csvHeader", "true");
            totalAccountsCsvAttributes.put("csvColumnName", "Alter");
            totalAccountsCsvAttributes.put("csvAggregationMethod", CsvAggregationMethod.MAXIMUM.toString());
            totalAccountsCsvAttributes.put("csvDelimiter", ";");
            totalAccountsCsv.setAttributes(totalAccountsCsvAttributes);
            totalAccountsCsv.setName("Total accounts (csv)");
            totalAccountsCsv.setLabel("totalAccountsCsv");
            totalAccountsCsv.setDescription("Total accounts in csv file");
            totalAccountsCsv.setAudiences(Set.of(cLevel, manager));
            totalAccountsCsv.setInformationNeeds(Set.of(businessFacilitation));
            totalAccountsCsv.setStakeholders(Set.of(ciso));
            totalAccountsCsv.setScale(Scale.RATIO);
            totalAccountsCsv.setUnit(Unit.TOTAL);
            totalAccountsCsv.setFrequency(new Frequency(Duration.parse("PT30S")));
            
            // average password length (manual)
            logger.info("average password length");
            Measurement averagePasswordLength = new Measurement();
            averagePasswordLength.setDataSource(manualDataSourcePasswordLength);
            averagePasswordLength.setName("Average password length");
            averagePasswordLength.setLabel("averagePasswordLength");
            averagePasswordLength.setDescription("Average length of passwords");
            averagePasswordLength.setAudiences(Set.of(cLevel, manager));
            averagePasswordLength.setInformationNeeds(Set.of(riskReduction));
            averagePasswordLength.setStakeholders(Set.of(ciso));
            averagePasswordLength.setScale(Scale.RATIO);
            averagePasswordLength.setUnit(Unit.TOTAL);
            averagePasswordLength.setFrequency(new Frequency(Duration.parse("PT1M")));
            
            // passwords (manual)
            logger.info("passwords");
            Measurement passwords = new Measurement();
            passwords.setDataSource(manualDataSourcePasswords);
            passwords.setName("Passwords");
            passwords.setLabel("passwords");
            passwords.setDescription("Total number of passwords");
            passwords.setAudiences(Set.of(cLevel, manager));
            passwords.setInformationNeeds(Set.of(riskReduction));
            passwords.setStakeholders(Set.of(ciso));
            passwords.setScale(Scale.RATIO);
            passwords.setUnit(Unit.TOTAL);
            passwords.setFrequency(new Frequency(Duration.parse("PT10S")));
            
            // weak passwords (manual)
            logger.info("weak passwords");
            Measurement weakPasswords = new Measurement();
            weakPasswords.setDataSource(manualDataSourceWeakPasswords);
            weakPasswords.setName("Weak passwords");
            weakPasswords.setLabel("weakPasswords");
            weakPasswords.setDescription("Total number of weak passwords");
            weakPasswords.setAudiences(Set.of(cLevel, manager));
            weakPasswords.setInformationNeeds(Set.of(riskReduction));
            weakPasswords.setStakeholders(Set.of(ciso));
            weakPasswords.setScale(Scale.RATIO);
            weakPasswords.setUnit(Unit.TOTAL);
            weakPasswords.setFrequency(new Frequency(Duration.parse("PT30S")));
            
            // leaked passwords (manual)
            logger.info("leaked passwords");
            Measurement leakedPasswords = new Measurement();
            leakedPasswords.setDataSource(manualDataSourceLeakedPasswords);
            leakedPasswords.setName("Leaked passwords");
            leakedPasswords.setLabel("leakedPasswords");
            leakedPasswords.setDescription("Total number of leaked passwords");
            leakedPasswords.setAudiences(Set.of(cLevel, manager));
            leakedPasswords.setInformationNeeds(Set.of(riskReduction));
            leakedPasswords.setStakeholders(Set.of(ciso));
            leakedPasswords.setScale(Scale.RATIO);
            leakedPasswords.setUnit(Unit.TOTAL);
            leakedPasswords.setFrequency(new Frequency(Duration.parse("PT90S")));
            
            // identical passwords (manual)
            logger.info("identical passwords");
            Measurement identicalPasswords = new Measurement();
            identicalPasswords.setDataSource(manualDataSourceIdenticalPasswords);
            identicalPasswords.setName("Identical passwords");
            identicalPasswords.setLabel("identicalPasswords");
            identicalPasswords.setDescription("Total number of identical passwords");
            identicalPasswords.setAudiences(Set.of(cLevel, manager));
            identicalPasswords.setInformationNeeds(Set.of(riskReduction));
            identicalPasswords.setStakeholders(Set.of(ciso));
            identicalPasswords.setScale(Scale.RATIO);
            identicalPasswords.setUnit(Unit.TOTAL);
            identicalPasswords.setFrequency(new Frequency(Duration.parse("PT1M")));

            // average password age in days (manual)
            logger.info("average password age");
            Measurement averagePasswordAge = new Measurement();
            averagePasswordAge.setDataSource(manualDataSourcePasswordAge);
            averagePasswordAge.setName("Average password age");
            averagePasswordAge.setLabel("averagePasswordAge");
            averagePasswordAge.setDescription("Average age of passwords");
            averagePasswordAge.setAudiences(Set.of(cLevel, manager));
            averagePasswordAge.setInformationNeeds(Set.of(riskReduction, businessFacilitation));
            averagePasswordAge.setStakeholders(Set.of(ciso));
            averagePasswordAge.setScale(Scale.RATIO);
            averagePasswordAge.setUnit(Unit.TOTAL);
            averagePasswordAge.setFrequency(new Frequency(Duration.parse("PT45S")));

            // total accounts (database)
            logger.info("total accounts (database)");
            Measurement totalAccountsDatabase = new Measurement();
            totalAccountsDatabase.setDataSource(databaseDataSourceTest);
            Map<String, String> totalAccountsDatabaseAttributes = new HashMap<>();
            totalAccountsDatabaseAttributes.put("sqlQuery", "SELECT COUNT(*) FROM users;");
            totalAccountsDatabase.setAttributes(totalAccountsDatabaseAttributes);
            totalAccountsDatabase.setName("Total accounts (database)");
            totalAccountsDatabase.setLabel("totalAccountsDatabase");
            totalAccountsDatabase.setDescription("Total accounts in database");
            totalAccountsDatabase.setAudiences(Set.of(cLevel, manager));
            totalAccountsDatabase.setInformationNeeds(Set.of(businessFacilitation));
            totalAccountsDatabase.setStakeholders(Set.of(ciso));
            totalAccountsDatabase.setScale(Scale.RATIO);
            totalAccountsDatabase.setUnit(Unit.TOTAL);
            totalAccountsDatabase.setFrequency(new Frequency(Duration.parse("PT30S")));

            logger.info("Saving measurements");
            Set<Measurement> measurements = Set.of(averageAge, maximumAge, totalAccountsCsv, totalAccountsDatabase, averagePasswordLength,
                    passwords, weakPasswords, leakedPasswords, identicalPasswords, averagePasswordAge);
            measurementRepository.saveAll(measurements);

            logger.info("Scheduling measurements");
            for (Measurement m : measurements) {
                jobSchedulingService.measureMeasurement(m);
            }

            // percentage of weak passwords
            Metric percentageOfWeakPasswords = new Metric();
            Formula percentageOfWeakPasswordsFormula = new Formula("{{weakPasswords}}/{{passwords}}");
            percentageOfWeakPasswordsFormula.setMeasurables(Set.of(weakPasswords, passwords));
            percentageOfWeakPasswords.setFormula(percentageOfWeakPasswordsFormula);
            percentageOfWeakPasswords.setName("Weak passwords");
            percentageOfWeakPasswords.setLabel("weakPasswordsPercentage");
            percentageOfWeakPasswords.setDescription("Percentage of weak passwords");
            percentageOfWeakPasswords.setAudiences(Set.of(cLevel, manager));
            percentageOfWeakPasswords.setInformationNeeds(Set.of(riskReduction));
            percentageOfWeakPasswords.setStakeholders(Set.of(ciso));
            percentageOfWeakPasswords.setScale(Scale.RATIO);
            percentageOfWeakPasswords.setUnit(Unit.PERCENT);
            percentageOfWeakPasswords.setTargetValue(BigDecimal.ZERO);
            percentageOfWeakPasswords.setFrequency(new Frequency(Duration.parse("PT10S")));

            // percentage of identical passwords
            Metric percentageOfIdenticalPasswords = new Metric();
            Formula percentageOfIdenticalPasswordsFormula = new Formula("{{identicalPasswords}}/{{passwords}}");
            percentageOfIdenticalPasswordsFormula.setMeasurables(Set.of(identicalPasswords, passwords));
            percentageOfIdenticalPasswords.setFormula(percentageOfIdenticalPasswordsFormula);
            percentageOfIdenticalPasswords.setName("Identical passwords");
            percentageOfIdenticalPasswords.setLabel("identicalPasswordsPercentage");
            percentageOfIdenticalPasswords.setDescription("Percentage of identical passwords");
            percentageOfIdenticalPasswords.setAudiences(Set.of(cLevel, manager));
            percentageOfIdenticalPasswords.setInformationNeeds(Set.of(riskReduction));
            percentageOfIdenticalPasswords.setStakeholders(Set.of(ciso));
            percentageOfIdenticalPasswords.setScale(Scale.RATIO);
            percentageOfIdenticalPasswords.setUnit(Unit.PERCENT);
            percentageOfIdenticalPasswords.setTargetValue(BigDecimal.ZERO);
            percentageOfIdenticalPasswords.setFrequency(new Frequency(Duration.parse("PT10S")));

            // percentage of leaked passwords
            Metric percentageOfLeakedPasswords = new Metric();
            Formula percentageOfLeakedPasswordsFormula = new Formula("{{leakedPasswords}}/{{passwords}}");
            percentageOfLeakedPasswordsFormula.setMeasurables(Set.of(leakedPasswords, passwords));
            percentageOfLeakedPasswords.setFormula(percentageOfLeakedPasswordsFormula);
            percentageOfLeakedPasswords.setName("Leaked passwords");
            percentageOfLeakedPasswords.setLabel("leakedPasswordsPercentage");
            percentageOfLeakedPasswords.setDescription("Percentage of leaked passwords");
            percentageOfLeakedPasswords.setAudiences(Set.of(cLevel, manager));
            percentageOfLeakedPasswords.setInformationNeeds(Set.of(riskReduction));
            percentageOfLeakedPasswords.setStakeholders(Set.of(ciso));
            percentageOfLeakedPasswords.setScale(Scale.RATIO);
            percentageOfLeakedPasswords.setUnit(Unit.PERCENT);
            percentageOfLeakedPasswords.setTargetValue(BigDecimal.ZERO);
            percentageOfLeakedPasswords.setFrequency(new Frequency(Duration.parse("PT10S")));

            // average password age
            Metric passwordAgeAverage = new Metric();
            Formula passwordAgeAverageFormula = new Formula("{{averagePasswordAge}}");
            passwordAgeAverageFormula.setMeasurables(Set.of(averagePasswordAge));
            passwordAgeAverage.setFormula(passwordAgeAverageFormula);
            passwordAgeAverage.setName("Password age");
            passwordAgeAverage.setLabel("passwordAgeAverage");
            passwordAgeAverage.setDescription("Average age of passwords");
            passwordAgeAverage.setAudiences(Set.of(cLevel, manager));
            passwordAgeAverage.setInformationNeeds(Set.of(riskReduction, businessFacilitation));
            passwordAgeAverage.setStakeholders(Set.of(ciso));
            passwordAgeAverage.setScale(Scale.RATIO);
            passwordAgeAverage.setUnit(Unit.TOTAL);
            passwordAgeAverage.setTargetValue(BigDecimal.valueOf(365));
            passwordAgeAverage.setFrequency(new Frequency(Duration.parse("PT10S")));

            // average password length
            Metric passwordLengthAverage = new Metric();
            Formula averagePasswordLengthFormula = new Formula("{{averagePasswordLength}}");
            averagePasswordLengthFormula.setMeasurables(Set.of(averagePasswordLength));
            passwordLengthAverage.setFormula(averagePasswordLengthFormula);
            passwordLengthAverage.setName("Password length");
            passwordLengthAverage.setLabel("passwordLengthAverage");
            passwordLengthAverage.setDescription("Average age of passwords");
            passwordLengthAverage.setAudiences(Set.of(cLevel, manager));
            passwordLengthAverage.setInformationNeeds(Set.of(riskReduction, businessFacilitation));
            passwordLengthAverage.setStakeholders(Set.of(ciso));
            passwordLengthAverage.setScale(Scale.RATIO);
            passwordLengthAverage.setUnit(Unit.TOTAL);
            passwordLengthAverage.setTargetValue(BigDecimal.valueOf(12));
            passwordLengthAverage.setFrequency(new Frequency(Duration.parse("PT10S")));

            // average password length
            Metric totalAccounts = new Metric();
            Formula totalAccountsFormula = new Formula("{{totalAccountsCsv}} + {{totalAccountsDatabase}}");
            totalAccountsFormula.setMeasurables(Set.of(totalAccountsCsv, totalAccountsDatabase));
            totalAccounts.setFormula(totalAccountsFormula);
            totalAccounts.setName("Total accounts");
            totalAccounts.setLabel("totalAccounts");
            totalAccounts.setDescription("Count all accounts");
            totalAccounts.setAudiences(Set.of(cLevel, manager));
            totalAccounts.setInformationNeeds(Set.of(businessFacilitation));
            totalAccounts.setStakeholders(Set.of(ciso));
            totalAccounts.setScale(Scale.RATIO);
            totalAccounts.setUnit(Unit.TOTAL);
            totalAccounts.setTargetValue(BigDecimal.valueOf(17));
            totalAccounts.setFrequency(new Frequency(Duration.parse("PT60S")));

            logger.info("Saving measurements");
            Set<Metric> metrics = Set.of(percentageOfWeakPasswords, percentageOfIdenticalPasswords,
                    percentageOfLeakedPasswords, passwordAgeAverage, passwordLengthAverage, totalAccounts);
            metricRepository.saveAll(metrics);

            logger.info("Scheduling metrics");
            for (Metric m : metrics) {
                jobSchedulingService.calculateMetric(m);
            }

        };
    }
}
