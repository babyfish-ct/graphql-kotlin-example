package org.frchen.graphql.example.config

import org.frchen.graphql.example.model.Employee
import org.frchen.graphql.example.model.Gender
import org.frchen.graphql.example.model.Location
import org.frchen.graphql.example.table.TDepartment
import org.frchen.graphql.example.table.TEmployee
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class DataInstaller: ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        val developId = TDepartment.insertAndGetId {
            it[name] = "Develop"
            it[location] = Location.BEIJING
        }.value
        val testId = TDepartment.insertAndGetId {
            it[name] = "Test"
            it[location] = Location.SHANGHAI
        }.value
        TDepartment.insert {
            it[name] = "New Department"
            it[location] = Location.CHENGDU
        }

        val jimId = TEmployee.insertAndGetId {
            it[name] = "Jim"
            it[gender] = Gender.MALE
            it[mobile] = "12345678901"
            it[departmentId] = developId
        }.value
        val kateId = TEmployee.insertAndGetId {
            it[name] = "Kate"
            it[gender] = Gender.FEMALE
            it[mobile] = "12345678902"
            it[departmentId] = developId
            it[supervisorId] = jimId
        }.value
        val bobId = TEmployee.insertAndGetId {
            it[name] = "Bob"
            it[gender] = Gender.MALE
            it[mobile] = "12345678903"
            it[departmentId] = developId
            it[supervisorId] = jimId
        }.value
        TEmployee.batchInsert(
            listOf(
                Employee(name = "Mary", gender = Gender.FEMALE, mobile = "12345678904", _supervisorId = kateId),
                Employee(name = "Herman", gender = Gender.MALE, mobile = "12345678905", _supervisorId = kateId),
                Employee(name = "Nancy", gender = Gender.FEMALE, mobile = "12345678906", _supervisorId = bobId),
                Employee(name = "Daniel", gender = Gender.MALE, mobile = "12345678907", _supervisorId = bobId)
            )
        ) {
            this[TEmployee.name] = it.name
            this[TEmployee.gender] = it.gender
            this[TEmployee.mobile] = it.mobile
            this[TEmployee.supervisorId] = it._supervisorId
            this[TEmployee.departmentId] = developId
        }

        val linaId = TEmployee.insertAndGetId {
            it[name] = "Lina"
            it[gender] = Gender.FEMALE
            it[mobile] = "12345678908"
            it[departmentId] = testId
        }.value
        val smithId = TEmployee.insertAndGetId {
            it[name] = "Smith"
            it[gender] = Gender.MALE
            it[mobile] = "12345678909"
            it[departmentId] = testId
            it[supervisorId] = linaId
        }.value
        val dariaId = TEmployee.insertAndGetId {
            it[name] = "Daria"
            it[gender] = Gender.FEMALE
            it[mobile] = "12345678910"
            it[departmentId] = testId
            it[supervisorId] = linaId
        }.value
        TEmployee.batchInsert(
            listOf(
                Employee(name = "Michael", gender = Gender.MALE, mobile = "12345678911", _supervisorId = smithId),
                Employee(name = "Sally", gender = Gender.FEMALE, mobile = "12345678912", _supervisorId = smithId),
                Employee(name = "Jack", gender = Gender.MALE, mobile = "12345678913", _supervisorId = dariaId),
                Employee(name = "Zena", gender = Gender.FEMALE, mobile = "12345678914", _supervisorId = dariaId)
            )
        ) {
            this[TEmployee.name] = it.name
            this[TEmployee.gender] = it.gender
            this[TEmployee.mobile] = it.mobile
            this[TEmployee.supervisorId] = it._supervisorId
            this[TEmployee.departmentId] = testId
        }
    }
}