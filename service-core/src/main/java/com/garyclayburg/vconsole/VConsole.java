/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2014 Gary Clayburg
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.garyclayburg.vconsole;

import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/7/14
 * Time: 7:43 AM
 *
 * @author Gary Clayburg
 */
//@VaadinUI(path = "/console")
@VaadinUI(path = "/start")  // maps to e.g.: localhost:8080/console via application.properties
public class VConsole extends UI {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(VConsole.class);

    @Autowired
    AutoUserRepo autoUserRepo;

    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);
        Button button = new Button("Clicker");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                layout.addComponent(new Label("thank you. may I have another.  yet one more"));
            }
        });
        layout.addComponent(button);
        List<User> allUsers = autoUserRepo.findAll();
        for (User allUser : allUsers) {
            layout.addComponent(new Label(allUser.getFirstname()));
        }

        Table staticTable = new Table();
        staticTable.setSizeFull();
        staticTable.setSelectable(true);
        staticTable.setMultiSelect(false);
        staticTable.setImmediate(true);

        createStatic(layout,staticTable);

        BeanContainer<String, User> userBeanContainer = new BeanContainer<String, User>(User.class);
        userBeanContainer.setBeanIdProperty("firstname");

        for (User user : allUsers) {
            userBeanContainer.addBean(user);
        }
        Table userTable = new Table();
        userTable.setSizeFull();
        userTable.setSelectable(true);
        userTable.setMultiSelect(false);
        userTable.setImmediate(true);
        userTable.setContainerDataSource(userBeanContainer);
        layout.addComponent(userTable);
    }

    private void createStatic(VerticalLayout layout,Table staticTable) {
        Container container = new IndexedContainer();
        container.addContainerProperty("first",String.class,"unknown");
        container.addContainerProperty("last",String.class,"na");
        Item r1  = container.addItem("row1");
        r1.getItemProperty("first").setValue("Frank");
        r1.getItemProperty("last").setValue("Clayburg");
        Item r2  = container.addItem("row2");
        r2.getItemProperty("first").setValue("Claribel");
        r2.getItemProperty("last").setValue("Bell");
        staticTable.setContainerDataSource(container);
        layout.addComponent(staticTable);
    }
}
