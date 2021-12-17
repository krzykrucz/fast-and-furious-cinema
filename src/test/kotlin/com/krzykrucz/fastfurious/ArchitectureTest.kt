package com.krzykrucz.fastfurious

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import io.kotest.core.spec.style.FreeSpec

class ArchitectureTest : FreeSpec({
    val rootPackageClasses = ClassFileImporter()
        .withImportOption(DO_NOT_INCLUDE_TESTS)
        .importPackagesOf(javaClass)

    "monolith modules should be independent" {
        slices().matching("..module.(*)..")
            .should().notDependOnEachOther()
            .check(rootPackageClasses)
    }

    "event bus should not depend on anything" {
        noClasses().that().resideInAPackage("..monolith..")
            .should().dependOnClassesThat().resideInAPackage("..module..")
            .check(rootPackageClasses)
    }

})