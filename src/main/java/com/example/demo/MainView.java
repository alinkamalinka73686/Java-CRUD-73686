package com.example.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

    private final CustomerRepository repo;

    private final Grid<Customer> grid = new Grid<>(Customer.class);
    private final TextField filter = new TextField();
    private final Button addNewBtn = new Button("New customer");

    private final TextField firstName = new TextField("First name");
    private final TextField lastName = new TextField("Last name");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final HorizontalLayout editor = new HorizontalLayout(firstName, lastName, save, delete);

    private Customer customer;

    public MainView(CustomerRepository repo) {
        this.repo = repo;

        filter.setPlaceholder("Filter by last name");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        editor.setAlignItems(Alignment.BASELINE);
        editor.setVisible(false);

        grid.setColumns("id", "firstName", "lastName");
        grid.asSingleSelect().addValueChangeListener(e -> editCustomer(e.getValue()));

        addNewBtn.addClickListener(e -> editCustomer(new Customer("", "")));

        save.addClickListener(e -> {
            customer.setFirstName(firstName.getValue());
            customer.setLastName(lastName.getValue());
            repo.save(customer);
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        delete.addClickListener(e -> {
            repo.delete(customer);
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        add(new HorizontalLayout(filter, addNewBtn), grid, editor);

        if (repo.count() == 0) {
            repo.save(new Customer("Jack", "Bauer"));
            repo.save(new Customer("Chloe", "O'Brian"));
            repo.save(new Customer("Kim", "Bauer"));
        }
        listCustomers(null);
    }

    private void editCustomer(Customer c) {
        if (c == null) {
            editor.setVisible(false);
            return;
        }
        customer = c;
        firstName.setValue(c.getFirstName() == null ? "" : c.getFirstName());
        lastName.setValue(c.getLastName() == null ? "" : c.getLastName());
        editor.setVisible(true);
        firstName.focus();
    }

    private void listCustomers(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
        }
    }
}