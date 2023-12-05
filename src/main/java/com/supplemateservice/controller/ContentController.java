package com.supplemateservice.controller;

import com.supplemateservice.model.Customers;
import com.supplemateservice.service.DeleteService;
import com.supplemateservice.service.LookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@CrossOrigin
public class ContentController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LookupService lookupService;

    @Autowired
    DeleteService deleteService;


    private Set<String> violations = new HashSet();

    @GetMapping("/content")
    public String displayContentPage(Model model, @AuthenticationPrincipal UserDetails currentCustomer) {
        Customers customer = lookupService.getCustomerByUsername(currentCustomer.getUsername());
        if (customer == null) {
            return "Oops, something went wrong.";
        }
        logger.info("Generating content for user: {}", customer.getUsername());
        model.addAttribute("currentCustomer", customer);

        return "dataView_content";
    }

    // used the longer @RequestMapping annotation just to compare to the shortcut versions
    @RequestMapping(value = "/addSupplement", method = RequestMethod.GET)
    public String displayAddMetrics(Model model, @AuthenticationPrincipal UserDetails currentCustomer){
        Customers customer = lookupService.getCustomerByUsername(currentCustomer.getUsername()); // CHANGE TO SERVICE METHOD
        model.addAttribute("currentCustomer", customer);
        return "addSupplement";
    }

    @GetMapping("/removeSupplement")
    public String displayRemoveMetrics(Model model, @AuthenticationPrincipal UserDetails currentCustomer){
        Customers customer = lookupService.getCustomerByUsername(currentCustomer.getUsername());
        model.addAttribute("typeList", lookupService.getSupplementTypesForCustomer(customer.getCustomerId()));
        return "removeSupplement";
    }

    @DeleteMapping("/deleteConfirmation")
    public String deleteConfirmation(String id, Model model){
        int typeId = Integer.parseInt(id);
        model.addAttribute("type", lookupService.getSupplementTypeById(typeId));
        model.addAttribute("numberOfEntries", lookupService.getSupplementEntriesForType(typeId).size());
        return "deleteConfirmation";
    }

    @GetMapping("/deleteSupplementType")
    public String performTypeDeletion(String id){
        int supplementTypeId = Integer.parseInt(id);
        // perform deletion
        deleteService.deleteSupplementType(supplementTypeId);
        return "redirect:/removeSupplement";
    }

    @GetMapping("/userSettings")
    public String displayUserSettings(){
        return "userSettings";
    }
}
