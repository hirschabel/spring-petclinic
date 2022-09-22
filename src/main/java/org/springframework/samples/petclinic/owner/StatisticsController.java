package org.springframework.samples.petclinic.owner;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.*;

@Controller
public class StatisticsController {

    private final PetRepository petRepo;


    public StatisticsController(PetRepository petRepo) {
        this.petRepo = petRepo;
    }

    private void fillResultList(List<Statistics> resultList, List<Pet> pets, Set<Integer> tipusId, boolean isOwnerList) {
        for (Integer id : tipusId) {
            String szoveg = "";
            int osszeg = 0;
            int darab = 0;
            for (Pet pet : pets) {
                if (id.equals(pet.getOwner().getId())) {
                    if (pet.getDeathDate() != null) {
                        osszeg += pet.getDeathDate().getYear() - pet.getBirthDate().getYear();
                    } else {
                        osszeg += LocalDate.now().getYear() - pet.getBirthDate().getYear();
                    }
                    darab++;
                    if (isOwnerList) {
                        szoveg = pet.getOwner().getFirstName() + ' ' + pet.getOwner().getLastName();
                    } else {
                        szoveg = pet.getType().getName();
                    }
                }
            }
            resultList.add(new Statistics(szoveg, osszeg / (double)darab));
        }
    }

    @GetMapping("/statistics/{tipus}")
    public ModelAndView showOwner(@PathVariable("tipus") String tipus) {
        ModelAndView mav = new ModelAndView("pets/petList");
        List<Pet> pets = petRepo.findAllPets();
        Set<Integer> tipusId = new HashSet<>();
        List<Statistics> resultList = new ArrayList<>();


        boolean isOwnerList = false;
        if (tipus.equals("Owner")) {
            for (Pet pet : pets) {
                tipusId.add(pet.getOwner().getId());
            }
            isOwnerList = true;
        } else if (tipus.equals("Type")) {
            for (Pet pet : pets) {
                tipusId.add(pet.getType().getId());
            }
        } else {
            return new ModelAndView("redirect:/welcome");
        }

        fillResultList(resultList, pets, tipusId, isOwnerList);
        PropertyComparator.sort(resultList, new MutableSortDefinition("average", true, true));
        mav.addObject("values", resultList);
        return mav;
    }
}
