package com.amazingco;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import es.uva.inf.poo.maps.GPSCoordinate;

public class PickingPointHubTest {
    private GPSCoordinate g;
    private LocalTime t;
    private Kiosk k1;
    private Kiosk k2;
    private PackageLocker pl1;
    private PackageLocker pl2;

    @Before
    public void init() {
        g = new GPSCoordinate(2.0, 3.0);
        t = LocalTime.of(12, 12);
        k1 = new Kiosk("123", g, t, 10);
        k2 = new Kiosk("124", g, t, 10);
        pl1 = new PackageLocker("125", g, t, 10);
        pl2 = new PackageLocker("126", g, t, 10);
    }

    @Test
    public void testValidConstructor() {
        GroupablePoint[] groupables = { k1, pl1 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        assertNotNull(pp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorGroupablesNull() {
        new PickingPointHub("111", g, t, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorGroupablesLessThan2() {
        GroupablePoint[] groupables = { k1 };
        new PickingPointHub("111", g, t, groupables);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorDuplicateIdentifiers() {
        PackageLocker plDuplicate = new PackageLocker("123", g, t, 10);
        GroupablePoint[] groupables = { k1, plDuplicate };
        new PickingPointHub("111", g, t, groupables);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorDifferentCoordinates() {
        GPSCoordinate g1 = new GPSCoordinate(2.0, 44.0);
        pl1 = new PackageLocker("124", g1, t, 10);
        GroupablePoint[] groupables = { k1, pl1 };
        new PickingPointHub("111", g, t, groupables);
    }

    @Test()
    public void testRemoveGroupableSuccess() {
        GroupablePoint[] groupables = { k1, pl1, k2, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.removeGroupable(k1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveGroupableNull() {
        GroupablePoint[] groupables = { k1, pl1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.removeGroupable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveGroupableLeavesLessThan2() {
        GroupablePoint[] groupables = { k1, pl1 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.removeGroupable(k1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveGroupableNotPresent() {
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.removeGroupable(k2);
    }

    @Test()
    public void testAddGroupableSuccess() {
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.addGroupable(k2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupableNull() {
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.addGroupable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupableDifferentCoordinates() {
        GPSCoordinate g2 = new GPSCoordinate(32.0, 23.0);
        Kiosk k3 = new Kiosk("1242", g2, t, 10);
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.addGroupable(k3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupableDuplicateIdentifierWhenFull() {
        Kiosk k3 = new Kiosk("123", g, t, 10);
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.addGroupable(k3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupableDuplicateIdentifierWhenHasSpace() {
        Kiosk k3 = new Kiosk("123", g, t, 10);
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.removeGroupable(pl1);
        pp.addGroupable(k3);
    }

    // AÑADIDO: Forzamos comportamiento sobre los huecos NULL del array
    @Test
    public void testMethodsWithNullElements() {
        GroupablePoint[] groupables = { k1, pl1, pl2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        
        // Creamos un hueco 'null' en medio del array
        pp.removeGroupable(pl1); 
        
        // Al ejecutar estos métodos, obligamos al código a entrar por los "if(groupables[i] != null)"
        assertTrue(pp.isSpaceAvailable());
        assertEquals(Integer.valueOf(2), pp.getGroupableCount());
        assertTrue(pp.isCashOnDelivery());
        
        // Llenamos el hueco
        Kiosk k3 = new Kiosk("999", g, t, 10);
        pp.addGroupable(k3); 
        assertEquals(Integer.valueOf(3), pp.getGroupableCount());
    }

    @Test()
    public void TestIsEspacioDisponible() {
        GroupablePoint[] groupables = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        assertTrue(pp.isSpaceAvailable());
        
        Package p = new Package(10, true, "1111111119");
        Kiosk kioskLleno = new Kiosk("12", g, t, 1);
        kioskLleno.addPackage(p, LocalDate.of(2023, 12, 12));
        
        PackageLocker plLleno = new PackageLocker("12312", g, t, 1);
        Package p2 = new Package(10, true, "1234567895");
        plLleno.addPackage(p2, LocalDate.of(2023, 12, 12));
        
        GroupablePoint[] groupables2 = { kioskLleno, plLleno };
        pp = new PickingPointHub("111", g, t, groupables2);
        assertFalse(pp.isSpaceAvailable());
    }

    @Test()
    public void TestGetCantidadGroupables() {
        GroupablePoint[] groupables = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        assertTrue(pp.getGroupableCount().equals(2));
        assertFalse(pp.getGroupableCount().equals(3));
    }

    @Test()
    public void TestIsElegibleContraReembolso() {
        GroupablePoint[] groupables = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        assertTrue(pp.isCashOnDelivery());
        
        GroupablePoint[] groupables3 = { pl1, pl2 };
        pp = new PickingPointHub("111", g, t, groupables3);
        assertFalse(pp.isCashOnDelivery());
    }

    @Test()
    public void TestSetOperativoCorrecto() {
        GroupablePoint[] groupables = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.setOperative(false);
        assertFalse(pp.isOperative());

        GroupablePoint[] groupables3 = { k2, pl2 };
        pp = new PickingPointHub("111", g, t, groupables3);
        k2.setOperative(false);
        pl2.setOperative(true);
        pp.setOperative(true);
        assertTrue(pp.isOperative());

        pl2.setOperative(false);
        pp.setOperative(true);
        assertFalse(pp.isOperative());
    }

    @Test(expected = IllegalArgumentException.class)
    public void TestSetOperativoNull() {
        GroupablePoint[] groupables = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        pp.setOperative(null);
    }

    @Test()
    public void TestIsCertificado() {
        GroupablePoint[] groupables = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        assertFalse(pp.isCertified());
    }

    // AÑADIDO: Testear overrides
    @Test
    public void testEqualsAndHashCode() {
        GroupablePoint[] gA = { k1, pl1 };
        GroupablePoint[] gB = { k1, pl1 };
        
        PickingPointHub ppA = new PickingPointHub("111", g, t, gA);
        PickingPointHub ppB = new PickingPointHub("111", g, t, gB);
        PickingPointHub ppC = new PickingPointHub("222", g, t, gA); // ID distinto
        
        assertTrue(ppA.equals(ppA));
        assertFalse(ppA.equals(null));
        assertFalse(ppA.equals(new Object()));
        assertTrue(ppA.equals(ppB));
        assertEquals(ppA.hashCode(), ppB.hashCode());
        assertFalse(ppA.equals(ppC));
    }

	@Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullElement() {
        // Forzamos un null DENTRO del array para cubrir los "if(groupable == null)"
        GroupablePoint[] arrConNull = { k1, null, k2 };
        new PickingPointHub("111", g, t, arrConNull);
    }

    @Test
    public void testEqualsAndNullsViaReflection() throws Exception {
        GroupablePoint[] arr = { k1, k2 };
        PickingPointHub pp = new PickingPointHub("111", g, t, arr);
        
        // Forzamos la rama falsa de instanceof
        assertFalse(pp.equals("No soy un Hub"));

        // HACK DE REFLECTION: Destruimos la matriz interna para forzar los "if(groupables == null)"
        java.lang.reflect.Field field = PickingPointHub.class.getDeclaredField("groupables");
        field.setAccessible(true);
        field.set(pp, null); // Obligamos a que la variable privada sea null

        // Ejecutamos todos los métodos que tienen la barrera defensiva "if (groupables == null)"
        assertFalse(pp.isSpaceAvailable());
        assertEquals(Integer.valueOf(0), pp.getGroupableCount());
        assertFalse(pp.isCashOnDelivery());
        
        // Forzamos el if(groupables != null) en setOperative
        pp.setOperative(false); 
    }

    @Test
    public void testToString() {
        GroupablePoint[] groupables = { k1, pl1 };
        PickingPointHub pp = new PickingPointHub("111", g, t, groupables);
        assertNotNull(pp.toString());
        assertTrue(pp.toString().contains("PickingPointHub"));
    }

}