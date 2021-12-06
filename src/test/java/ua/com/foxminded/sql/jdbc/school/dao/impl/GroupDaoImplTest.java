package ua.com.foxminded.sql.jdbc.school.dao.impl;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.sql.jdbc.school.model.Group;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GroupDaoImplTest extends DaoTest {

    @Test
    void shouldFindAll() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            List<Group> groups = groupDao.findAll(con);
            assertEquals(1, groups.size());
            Group foundGroup = groups.get(0);
            Group expectedGroup = new Group(foundGroup.getId(), GROUP_NAME);
            assertEquals(expectedGroup, foundGroup);
        }
    }

    @Test
    void shouldUpdate() throws SQLException {
        try (Connection con = datasource.getConnection()) {
            Group foundGroup = groupDao.findAll(con).get(0);
            Long id = foundGroup.getId();
            assertNotNull(id);
            Group updatedGroup = new Group(id, NEW_GROUP_NAME);
            groupDao.save(con, updatedGroup);
            assertEquals(updatedGroup, groupDao.findAll(con).get(0));
        }
    }

    @Test
    void shouldFindById() throws SQLException  {
        try (Connection con = datasource.getConnection()) {
            Group foundGroup = groupDao.findAll(con).get(0);
            Long id = foundGroup.getId();
            assertNotNull(id);
            Optional<Group> foundGroupDouble = groupDao.findById(con, id);
            assertTrue(foundGroupDouble.isPresent());
            assertEquals(foundGroup, foundGroupDouble.get());
            groupDao.deleteById(con, id);
            assertTrue(groupDao.findById(con, id).isEmpty());
        }
    }

    @Test
    void shouldFindByName() throws SQLException  {
        try (Connection con = datasource.getConnection()) {
            Optional<Group> foundGroup = groupDao.findByName(con, GROUP_NAME);
            assertTrue(foundGroup.isPresent());
            assertEquals(GROUP_NAME, foundGroup.get().getName());
        }
    }

    @Test
    void shouldFindAllDeleteByIdAndCreate() throws SQLException  {
        try (Connection con = datasource.getConnection()) {
            Group foundGroup = groupDao.findAll(con).get(0);
            Long id = foundGroup.getId();
            assertNotNull(id);
            groupDao.deleteById(con, id);
            assertTrue(groupDao.findAll(con).isEmpty());
            Group newGroup = new Group(NEW_GROUP_NAME);
            groupDao.save(con, newGroup);
            List<Group> groups = groupDao.findAll(con);
            assertEquals(1, groups.size());
            foundGroup = groups.get(0);
            newGroup.setId(foundGroup.getId());
            assertEquals(newGroup, foundGroup);
        }
    }
}