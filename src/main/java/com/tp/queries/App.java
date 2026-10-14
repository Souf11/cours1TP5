package com.tp.queries;

import com.tp.queries.model.Reservation;
import com.tp.queries.model.Salle;
import com.tp.queries.repository.SalleRepository;
import com.tp.queries.repository.SalleRepositoryImpl;
import com.tp.queries.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Salle s1 = new Salle("Salle Horizon", 25);
        Salle s2 = new Salle("Salle Rubis", 10);
        Salle s3 = new Salle("Salle Emeraude", 40);
        Salle s4 = new Salle("Amphitheatre Turing", 120);
        Salle s5 = new Salle("Salle Cristal", 18);

        em.persist(s1);
        em.persist(s2);
        em.persist(s3);
        em.persist(s4);
        em.persist(s5);

        LocalDateTime jour = LocalDateTime.of(2024, 10, 20, 0, 0);

        Reservation r1 = new Reservation(s1, jour.withHour(9), jour.withHour(12));
        Reservation r2 = new Reservation(s4, jour.withHour(14), jour.withHour(17));

        em.persist(r1);
        em.persist(r2);

        em.getTransaction().commit();

        SalleRepository repository = new SalleRepositoryImpl(em);

        LocalDateTime creneauDebut = jour.withHour(10);
        LocalDateTime creneauFin = jour.withHour(11).withMinute(30);

        System.out.println("--- Salles disponibles entre 10h00 et 11h30 ---");
        List<Salle> disponibles = repository.findAvailableRooms(creneauDebut, creneauFin);
        for (Salle s : disponibles) {
            System.out.println("- " + s.getNom() + " (capacite: " + s.getCapacite() + ")");
        }

        System.out.println("\n--- Recherche multi-criteres : nom contenant 'Salle' et capacite >= 20 ---");
        List<Salle> filtrees = repository.searchRooms("Salle", 20, creneauDebut, creneauFin, 1, 10);
        for (Salle s : filtrees) {
            System.out.println("- " + s.getNom() + " (capacite: " + s.getCapacite() + ")");
        }

        System.out.println("\n--- Test de pagination (page 1, taille 2) ---");
        long total = repository.countRooms(null, null, null, null);
        List<Salle> page1 = repository.searchRooms(null, null, null, null, 1, 2);
        PaginationResult<Salle> resultPage1 = new PaginationResult<>(page1, 1, 2, total);

        System.out.println("Page " + resultPage1.getCurrentPage() + "/" + resultPage1.getTotalPages() + " (total items: " + resultPage1.getTotalItems() + ") :");
        for (Salle s : resultPage1.getItems()) {
            System.out.println("- " + s.getNom());
        }

        System.out.println("\n--- Test de pagination (page 2, taille 2) ---");
        List<Salle> page2 = repository.searchRooms(null, null, null, null, 2, 2);
        PaginationResult<Salle> resultPage2 = new PaginationResult<>(page2, 2, 2, total);

        System.out.println("Page " + resultPage2.getCurrentPage() + "/" + resultPage2.getTotalPages() + " :");
        for (Salle s : resultPage2.getItems()) {
            System.out.println("- " + s.getNom());
        }

        em.close();
        emf.close();
    }
}
