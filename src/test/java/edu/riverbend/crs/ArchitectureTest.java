package edu.riverbend.crs;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "edu.riverbend.crs", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule domainIsIndependent = noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..web..", "..service..", "..repository..", "..config..", "org.springframework..");
    @ArchTest
    static final ArchRule servicesAvoidHttpAndSql = noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAnyPackage("..web..", "..repository.jdbc..", "..repository.memory..", "org.springframework.jdbc..", "org.springframework.http..", "org.springframework.web..", "java.sql..");
    @ArchTest
    static final ArchRule repositoriesAvoidUpperLayers = noClasses().that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAnyPackage("..service..", "..web..", "..config..");
    @ArchTest
    static final ArchRule webAvoidsRepositories = noClasses().that().resideInAPackage("..web..")
            .should().dependOnClassesThat().resideInAnyPackage("..repository..", "java.sql..", "org.springframework.jdbc..");
    @ArchTest
    static final ArchRule dtoAvoidsDomain = noClasses().that().resideInAPackage("..web.dto..")
            .should().dependOnClassesThat().resideInAnyPackage("..domain..", "..repository..", "..service..");
}
