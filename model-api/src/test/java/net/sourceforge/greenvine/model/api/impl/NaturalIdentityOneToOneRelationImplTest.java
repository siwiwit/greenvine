package net.sourceforge.greenvine.model.api.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;
import net.sourceforge.greenvine.model.api.ColumnType;
import net.sourceforge.greenvine.model.api.Entity;
import net.sourceforge.greenvine.model.api.ModelException;
import net.sourceforge.greenvine.model.api.OneToOneChildNaturalIdentity;
import net.sourceforge.greenvine.model.api.OneToOneParentField;
import net.sourceforge.greenvine.model.api.PropertyType;
import net.sourceforge.greenvine.model.api.PropertyValueGenerationStrategy;
import net.sourceforge.greenvine.model.naming.testutils.NamingConventionFactory;

import org.junit.Before;
import org.junit.Test;

public class NaturalIdentityOneToOneRelationImplTest {
    
    private CatalogImpl catalog;
    private EntityImpl ownerEntity;
    private Entity inverseEntity;
    private DatabaseImpl database;
    private TableImpl parent;
    private TableImpl child;
    private ForeignKeyImpl foreignKey;
    private UniqueKeyImpl uniqueKey;
    
    @Before
    public void setUp() throws ModelException {
        
        // Set up model and database
        ModelImpl model = new ModelImpl("test");
        this.database = new DatabaseImpl("TEST_DB", NamingConventionFactory.getTestNamingConvention());
        this.catalog = new CatalogImpl("testModel", model, database);
        
        // Parent table
        parent = database.createTable("PARENT");
        ColumnImpl parentPk1 = parent.createColumn("PARENT_ID_1", ColumnType.INTEGER, true, 4, 0);
        ColumnImpl parentPk2 = parent.createColumn("PARENT_ID_2", ColumnType.DATE, true, 4, 0);
        ColumnImpl parentData = parent.createColumn("DATA", ColumnType.VARCHAR, false, 50, 0);
        SortedSet<CharSequence> parentPkCols = new TreeSet<CharSequence>();
        parentPkCols.add(parentPk1.getName());
        parentPkCols.add(parentPk2.getName());
        parent.createPrimaryKey("PARENT_PK", parentPkCols);
        
        // Child table
        child = database.createTable("CHILD");
        ColumnImpl childPk1 = child.createColumn("CHILD_ID", ColumnType.INTEGER, true, 4, 0);
        ColumnImpl childUk1 = child.createColumn("CHILD_UK_1", ColumnType.INTEGER, true, 4, 0);
        ColumnImpl childUk2 = child.createColumn("CHILD_UK_2", ColumnType.DATE, true, 4, 0);
        SortedSet<CharSequence> childUkCols = new TreeSet<CharSequence>();
        childUkCols.add(childUk1.getName());
        childUkCols.add(childUk2.getName());
        uniqueKey = child.createUniqueKey("CHILD_UK", childUkCols);
        SortedSet<CharSequence> childPkCols = new TreeSet<CharSequence>();
        childPkCols.add(childPk1.getName());
        child.createPrimaryKey("CHILD_PK", childPkCols);
        
        // Foreign keys
        Map<CharSequence, CharSequence> parentConstraints = new HashMap<CharSequence, CharSequence>();
        parentConstraints.put(childUk1.getName(), parentPk1.getName());
        parentConstraints.put(childUk2.getName(), parentPk2.getName());
        foreignKey = database.createForeignKey("parent_fk", "CHILD", "PARENT", parentConstraints);
        
        // Entities
        ownerEntity = catalog.createEntity(null, "ownerEntity", parent.getName());
        ComponentIdentityImpl parentId = ownerEntity.createComponentIdentity("manyToOneOwnerEntityIdentity");
        parentId.createSimpleProperty("ownerIdField1", parentPk1.getColumnType().getPropertyType(), PropertyValueGenerationStrategy.ASSIGNED, parentPk1.getName());
        parentId.createSimpleProperty("ownerIdField2", parentPk2.getColumnType().getPropertyType(), PropertyValueGenerationStrategy.ASSIGNED, parentPk2.getName());
        ownerEntity.createSimpleProperty("data", PropertyType.STRING, false, parentData.getName());
        inverseEntity = catalog.createEntity(null, "inverseEntity", child.getName());
    }
    
    @Test
    public void testCreateValidBiDirectional() throws ModelException {
        OneToOneNaturalIdentityRelationImpl relation = new OneToOneNaturalIdentityRelationImpl("ownerField", "inverseField", foreignKey.getName(), uniqueKey.getName(), catalog);
        
        // Test relation
        Assert.assertEquals(ownerEntity, relation.getParentEntity());
        Assert.assertEquals(inverseEntity, relation.getChildEntity());
        Assert.assertEquals(foreignKey, relation.getForeignKey());
        
        // Test owner field
        Assert.assertNotNull(relation.getParentField());
        Assert.assertEquals("ownerField", relation.getParentField().getName().toString());
        Assert.assertEquals(ownerEntity, relation.getParentField().getFieldCollection());
        OneToOneParentField ownerField = ownerEntity.getOneToOneParent("ownerField");
        Assert.assertEquals(ownerField, relation.getParentField());
        
        // Test constrained identity field
        Assert.assertNotNull(relation.getChildField());
        Assert.assertEquals("inverseField", relation.getChildField().getName().toString());
        Assert.assertEquals(inverseEntity, relation.getChildField().getFieldCollection());
        Assert.assertEquals(relation.getChildField(), inverseEntity.getNaturalIdentity());

    }
    
    @Test
    public void testCreateValidUniDirectional() throws ModelException {
        OneToOneNaturalIdentityRelationImpl relation = new OneToOneNaturalIdentityRelationImpl("inverseField", foreignKey.getName(), uniqueKey.getName(), catalog);
        
        // Test relation
        Assert.assertEquals(ownerEntity, relation.getParentEntity());
        Assert.assertEquals(inverseEntity, relation.getChildEntity());
        Assert.assertEquals(foreignKey, relation.getForeignKey());
        
        // Test owner field
        Assert.assertNull(relation.getParentField());
        
        // Test inverse field
        Assert.assertNotNull(relation.getChildField());
        Assert.assertEquals("inverseField", relation.getChildField().getName().toString());
        Assert.assertEquals(inverseEntity, relation.getChildField().getFieldCollection());
        Assert.assertEquals(relation.getChildField(), inverseEntity.getNaturalIdentity());
        
    }
    
    
    
    @Test
    public void testBuildFromForeignKey() throws ModelException {
        OneToOneNaturalIdentityRelationImpl relation = new OneToOneNaturalIdentityRelationImpl("parentFk", "child", foreignKey.getName(), uniqueKey.getName(), catalog);
        
        // Test relation
        Assert.assertEquals(ownerEntity, relation.getParentEntity());
        Assert.assertEquals(inverseEntity, relation.getChildEntity());
        Assert.assertEquals(foreignKey, relation.getForeignKey());
        
        // Test owner field
        Assert.assertNotNull(relation.getParentField());
        Assert.assertEquals("parentFk", relation.getParentField().getName().toString());
        Assert.assertEquals(ownerEntity, relation.getParentField().getFieldCollection());
        OneToOneParentField ownerField = ownerEntity.getOneToOneParent("parentFk");
        Assert.assertEquals(ownerField, relation.getParentField());
        
        // Test inverse field
        Assert.assertNotNull(relation.getChildField());
        Assert.assertEquals("child", relation.getChildField().getName().toString());
        Assert.assertEquals(inverseEntity, relation.getChildField().getFieldCollection());
        Assert.assertNotNull(inverseEntity.getNaturalIdentity());
        Assert.assertTrue(inverseEntity.getNaturalIdentity() instanceof OneToOneChildNaturalIdentity);
        Assert.assertEquals(relation.getChildField(), inverseEntity.getNaturalIdentity());
        
    }
    
}
