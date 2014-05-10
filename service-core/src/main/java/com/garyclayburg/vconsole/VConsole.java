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

import com.garyclayburg.attributes.AttributeService;
import com.garyclayburg.attributes.GeneratedAttributesBean;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @SuppressWarnings("SpringJavaAutowiringInspection")  // IntelliJ confused by spring-boot wiring
    private AutoUserRepo autoUserRepo;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private AttributeService attributeService;
    private Map<String, Window> targetWindows;

    public VConsole() {
        targetWindows = new HashMap<String, Window>();
    }

    protected void init(VaadinRequest vaadinRequest) {
        targetWindows = new HashMap<String, Window>();
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        setContent(layout);

        List<User> allUsers = autoUserRepo.findAll();

        BeanContainer<String, User> userBeanContainer = new BeanContainer<String, User>(User.class);
        userBeanContainer.setBeanIdProperty("firstname");
        User firstUser = allUsers.get(0);
        for (User user : allUsers) {
            userBeanContainer.addBean(user);
        }
        Table userTable = createUserTable(userBeanContainer);
        final Label attributeLabel = new Label("attribute list here");
        final Table attributeTable = new Table();
        attributeTable.setSizeFull();
        attributeTable.setSelectable(true);
        attributeTable.setMultiSelect(false);
        attributeTable.setImmediate(true);

        final BeanContainer<String, GeneratedAttributesBean> attributesBeanContainer =
                new BeanContainer<String, GeneratedAttributesBean>(GeneratedAttributesBean.class);
        attributesBeanContainer.setBeanIdProperty("attributeName");
        populateItems(firstUser,attributesBeanContainer);

        attributeTable.setContainerDataSource(attributesBeanContainer);

        userTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                User selectedUser = (User) ((BeanItem) event.getItem()).getBean();
                attributeLabel.setValue("we have " + selectedUser.getFirstname() + " " + selectedUser.getLastname());
                populateItems(selectedUser,attributesBeanContainer);

                Set<String> entitledTargets = attributeService.getEntitledTargets(selectedUser);
                for (String entitledTarget : entitledTargets) {
                    populateTargetWindow(selectedUser,entitledTarget);

                }
            }
        });

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSizeFull();
        splitPanel.setSplitPosition(150,Unit.PIXELS);
        splitPanel.setFirstComponent(userTable);
        splitPanel.setSecondComponent(attributeTable);

        layout.addComponent(splitPanel);
    }

    private void populateTargetWindow(User selectedUser,String entitledTarget) {
        boolean addNewWindow = false;
        Window window = targetWindows.get(entitledTarget);
        if (window == null){
            window = new Window(entitledTarget);
            targetWindows.put(entitledTarget,window);
            addNewWindow = true;
        }

        VerticalLayout windowContent = new VerticalLayout();
        windowContent.setMargin(false);

        final Table attributeTargetTable = new Table();
        attributeTargetTable.setSizeFull();
        attributeTargetTable.setSelectable(true);
        attributeTargetTable.setMultiSelect(false);
        attributeTargetTable.setImmediate(true);
        final BeanContainer<String, GeneratedAttributesBean> attributesBeanContainer =
                new BeanContainer<String, GeneratedAttributesBean>(GeneratedAttributesBean.class);
        attributesBeanContainer.setBeanIdProperty("attributeName");
        populateItems(selectedUser,attributesBeanContainer,entitledTarget);

        attributeTargetTable.setContainerDataSource(attributesBeanContainer);

        windowContent.addComponent(attributeTargetTable);
        window.setContent(windowContent);
        if (addNewWindow){
            UI.getCurrent().addWindow(window);
        }
    }

    private void populateItems(User firstUser,BeanContainer<String, GeneratedAttributesBean> generatedAttributesBeanContainer) {
        List<GeneratedAttributesBean> generatedAttributes = attributeService.getGeneratedAttributesBean(firstUser);
        generatedAttributesBeanContainer.removeAllItems();
        for (GeneratedAttributesBean generatedAttribute : generatedAttributes) {
            generatedAttributesBeanContainer.addBean(generatedAttribute);
        }
    }

    private void populateItems(User firstUser,BeanContainer<String, GeneratedAttributesBean> generatedAttributesBeanContainer,String targetName) {
        List<GeneratedAttributesBean> generatedAttributes = attributeService.getGeneratedAttributesBean(firstUser,targetName);
        generatedAttributesBeanContainer.removeAllItems();
        for (GeneratedAttributesBean generatedAttribute : generatedAttributes) {
            generatedAttributesBeanContainer.addBean(generatedAttribute);
        }
    }

    private Table createUserTable(BeanContainer<String, User> userBeanContainer) {
        Table userTable = new Table();
        userTable.setSizeFull();
        userTable.setSelectable(true);
        userTable.setMultiSelect(false);
        userTable.setImmediate(true);
        userTable.setContainerDataSource(userBeanContainer);
//        userTable.setVisibleColumns(new Object[]{"firstname","lastname"});
        return userTable;
    }

    private void createStatic(VerticalLayout layout) {
        Table staticTable = new Table();
        staticTable.setSizeFull();
        staticTable.setSelectable(true);
        staticTable.setMultiSelect(false);
        staticTable.setImmediate(true);

        Container container = new IndexedContainer();
        container.addContainerProperty("first",String.class,"unknown");
        container.addContainerProperty("last",String.class,"na");
        Item r1 = container.addItem("row1");
        r1.getItemProperty("first")
                .setValue("Frank");
        r1.getItemProperty("last")
                .setValue("Clayburg");
        Item r2 = container.addItem("row2");
        r2.getItemProperty("first")
                .setValue("Claribel");
        r2.getItemProperty("last")
                .setValue("Bell");
        staticTable.setContainerDataSource(container);
        layout.addComponent(staticTable);
    }
}
