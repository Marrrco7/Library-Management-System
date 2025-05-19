package org.example.gui;

import org.example.model.Publisher;
import org.example.service.PublisherService;

import javax.swing.*;
import java.awt.*;


/**
 * A dialog for adding or editing publisher records in the library system.
 *
 * <p>This dialog allows input of publisher details like name, address, and phone number.
 * It interacts with {@link PublisherService} to create new publisher entries or update
 * existing ones. Fields must be fully filled before saving.</p>
 *
 * <p>The dialog uses swing components to capture user input and perform
 * save or cancel actions. On saving, it will either add a new publisher or update an
 * existing one based on whether a publisher ID is set or not</p>
 */
public class AddEditPublisherDialog extends JDialog {

    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneNumberField;
    private PublisherService publisherService;
    private int publisherId = -1;

    public AddEditPublisherDialog(Frame parent, PublisherService publisherService) {
        super(parent, "Add/Edit Publisher", true);
        this.publisherService = publisherService;

        setLayout(new GridLayout(4, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        add(phoneNumberField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> savePublisher());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public void setPublisher(int id, String name, String address, String phoneNumber) {
        this.publisherId = id;
        nameField.setText(name);
        addressField.setText(address);
        phoneNumberField.setText(phoneNumber);
    }

    private void savePublisher() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (publisherId == -1) {
            publisherService.createPublisher(name, address, phoneNumber);
            JOptionPane.showMessageDialog(this, "Publisher added successfully.");
        } else {
            publisherService.updatePublisher(publisherId, name, address, phoneNumber);
            JOptionPane.showMessageDialog(this, "Publisher updated successfully.");
        }

        dispose();
    }
}
