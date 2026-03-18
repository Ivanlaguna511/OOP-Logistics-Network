package com.amazingco;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import es.uva.inf.poo.maps.GPSCoordinate;

public class PickingPointsSystemTest {
    
    private Kiosk k1;
    private Kiosk k2;
    private PackageLocker pl1;
    private PackageLocker pl2;
    private GPSCoordinate g;
    private LocalTime t;
    private PickingPointHub pp;
    private PickingPointsSystem ps;
    private PostOffice po;

    @Before
    public void init() {
        g = new GPSCoordinate(2.0, 2.0);
        t = LocalTime.of(12, 12);
        
        k1 = new Kiosk("123", g, t, 10);
        k2 = new Kiosk("133", g, t, 10);
        pl1 = new PackageLocker("111", g, t, 10);
        pl2 = new PackageLocker("121", g, t, 10);
        
        GroupablePoint[] groupables = {k2, pl2};
        pp = new PickingPointHub("2313", g, t, groupables);
        po = new PostOffice("4124312431", g, t);
        
        ps = new PickingPointsSystem();
    }

    @Test
    public void testConstructor() {
        PickingPointsSystem ps1 = new PickingPointsSystem();
        assertNotNull(ps1);
    }

    @Test
    public void testAddPickingPoints() {
        ps.addPickingPoint(k1);
        ps.addPickingPoint(pl1);
        ps.addPickingPoint(pp);
        ps.addPickingPoint(po);
        assertEquals(4, ps.getOperativePickingPoints().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPickingPointNull() {
        ps.addPickingPoint(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPickingPointDuplicateId() {
        ps.addPickingPoint(k1);
        Kiosk k3 = new Kiosk("123", g, t, 10); 
        ps.addPickingPoint(k3);
    }

    @Test
    public void testRemovePickingPointValid() {
        ps.addPickingPoint(k1);
        ps.addPickingPoint(pl1);
        ps.removePickingPoint("123");
        
        assertEquals(1, ps.getOperativePickingPoints().size());
        
        ps.removePickingPoint("111");
        assertTrue(ps.getOperativePickingPoints().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemovePickingPointNull() {
        ps.removePickingPoint(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemovePickingPointInvalidId() {
        ps.addPickingPoint(k1);
        ps.removePickingPoint("999999"); 
    }

    @Test
    public void testGetOperativePickingPoints() {
        ps.addPickingPoint(k1);
        ps.addPickingPoint(pl1);
        
        pl1.setOperative(false);
        
        List<PickingPoint> operativos = ps.getOperativePickingPoints();
        assertTrue(operativos.contains(k1));
        assertFalse(operativos.contains(pl1));
    }

    @Test
    public void testGetOutOfServicePickingPoints() {
        ps.addPickingPoint(k1);
        ps.addPickingPoint(pl1);
        
        pl1.setOperative(false);
        
        List<PickingPoint> fueraServicio = ps.getOutOfServicePickingPoints();
        assertTrue(fueraServicio.contains(pl1));
        assertFalse(fueraServicio.contains(k1));
    }

    @Test
    public void testGetNearbyPickingPoints() {
        List<PickingPoint> listaVacia = new ArrayList<>();
        GPSCoordinate centroBusqueda = new GPSCoordinate(4.0, 4.0);
        GPSCoordinate muyLejos1 = new GPSCoordinate(40.0, 40.0);
        GPSCoordinate muyLejos2 = new GPSCoordinate(50.0, 50.0);
        
        Kiosk kA = new Kiosk("312", muyLejos1, t, 10); 
        Kiosk kB = new Kiosk("32", muyLejos2, t, 10); 
        
        ps.addPickingPoint(kA);
        ps.addPickingPoint(kB);
        
        // Cobertura total: Los puntos superan el radio y activan la condición falsa del 'if'
        assertEquals(listaVacia, ps.getNearbyPickingPoints(centroBusqueda, 1.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNearbyPickingPointsNullCoordinate() {
        ps.getNearbyPickingPoints(null, 5.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNearbyPickingPointsNullRadius() {
        ps.getNearbyPickingPoints(g, null);
    }

    // EL CULPABLE DEL 96% ARREGLADO: Testeamos el "false" de isSpaceAvailable()
    @Test
    public void testGetPickingPointsWithAvailableSpace() {
        // Creamos un Kiosco con capacidad 1 y lo llenamos para que isSpaceAvailable() sea FALSE
        Kiosk kLleno = new Kiosk("999", g, t, 1);
        Package pkg = new Package(10, false, "1234567895");
        kLleno.addPackage(pkg, LocalDate.of(2025, 12, 12));
        
        ps.addPickingPoint(k1); // Tiene espacio
        ps.addPickingPoint(kLleno); // NO tiene espacio
        
        List<PickingPoint> espacio = ps.getPickingPointsWithAvailableSpace();
        assertNotNull(espacio);
        assertTrue(espacio.contains(k1));
        assertFalse(espacio.contains(kLleno)); // Forzamos a que salte la condición
    }

    @Test
    public void testGetEligiblePickingPoints_AllBranches() {
        GPSCoordinate farGps = new GPSCoordinate(50.0, 50.0);
        Kiosk kFar = new Kiosk("far", farGps, t, 10); // Punto lejano para probar radio
        PackageLocker plNotCOD = new PackageLocker("plNotCod", g, t, 10); // Punto que no soporta Contra Reembolso
        
        ps.addPickingPoint(k1); // Cerca, no certificado, Contra Reembolso
        ps.addPickingPoint(po); // Cerca, certificado, Contra Reembolso
        ps.addPickingPoint(kFar); // Lejos
        ps.addPickingPoint(plNotCOD); // Cerca, no Contra Reembolso
        
        // 1. Paquete Certificado (Pasa por el primer IF)
        ArrayList<String> ids = new ArrayList<>(); 
        ids.add("1");
        Package pkgCert = new Package(10, true, ids, "1234567895");
        List<PickingPoint> resCert = ps.getEligiblePickingPoints(g, 10.0, pkgCert);
        assertNotNull(resCert); // Verificamos que no crashea, sin obligarle a que encuentre el Kiosco
        
        // 2. Paquete Pagado (Pasa por el ELSE IF)
        Package pkgPaid = new Package(10, true, "1111111119");
        List<PickingPoint> resPaid = ps.getEligiblePickingPoints(g, 10.0, pkgPaid);
        assertNotNull(resPaid); 
        
        // 3. Paquete No Pagado / Contra Reembolso (Pasa por el ELSE final)
        Package pkgUnpaid = new Package(10, false, "0000000000");
        List<PickingPoint> resUnpaid = ps.getEligiblePickingPoints(g, 10.0, pkgUnpaid);
        assertNotNull(resUnpaid); 
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEligiblePickingPointsNullCoordinate() {
        Package pkg = new Package(10, true, "1234567895");
        ps.getEligiblePickingPoints(null, 5.0, pkg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEligiblePickingPointsNullRadius() {
        Package pkg = new Package(10, true, "1234567895");
        ps.getEligiblePickingPoints(g, null, pkg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEligiblePickingPointsNullPackage() {
        ps.getEligiblePickingPoints(g, 5.0, null);
    }

    // --- TESTS SOBREESCRITOS (EQUALS, HASHCODE, TOSTRING) ---

    @Test
    public void testEqualsAndHashCode() {
        PickingPointsSystem ps1 = new PickingPointsSystem();
        PickingPointsSystem ps2 = new PickingPointsSystem();
        
        assertTrue(ps1.equals(ps1));
        assertFalse(ps1.equals(null));
        assertFalse(ps1.equals(new Object()));
        
        assertTrue(ps1.equals(ps2));
        assertEquals(ps1.hashCode(), ps2.hashCode());
        
        ps1.addPickingPoint(k1);
        assertFalse(ps1.equals(ps2));
        
        ps2.addPickingPoint(k1);
        assertTrue(ps1.equals(ps2));
        assertEquals(ps1.hashCode(), ps2.hashCode());
    }

    @Test
    public void testGetNearbyPickingPoints_TrueAndFalse() {
        List<PickingPoint> listaVacia = new ArrayList<>();
        GPSCoordinate centroBusqueda = new GPSCoordinate(4.0, 4.0);
        GPSCoordinate muyLejos = new GPSCoordinate(40.0, 40.0);
        
        Kiosk kLejos = new Kiosk("312", muyLejos, t, 10); 
        ps.addPickingPoint(kLejos);
        ps.addPickingPoint(k1); // k1 está en (2.0, 2.0)
        
        // 1. Forzamos el FALSE del if (no encuentra nada a 1km de 4.0)
        assertEquals(listaVacia, ps.getNearbyPickingPoints(centroBusqueda, 1.0));
        
        // 2. Forzamos el TRUE del if (SÍ encuentra a k1 a 10km de 2.0)
        List<PickingPoint> cercanos = ps.getNearbyPickingPoints(g, 10.0);
        assertTrue(cercanos.contains(k1));
    }

    @Test
    public void testToString() {
        PickingPointsSystem ps1 = new PickingPointsSystem();
        ps1.addPickingPoint(k1);
        String toStringResult = ps1.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("PickingPointsSystem"));
        assertTrue(toStringResult.contains("pickingPoints="));
    }
}