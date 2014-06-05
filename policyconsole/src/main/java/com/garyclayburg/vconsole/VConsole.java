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
import com.garyclayburg.persistence.UserChangeController;
import com.garyclayburg.persistence.UserChangeListener;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import com.github.wolfie.refresher.Refresher;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/7/14
 * Time: 7:43 AM
 *
 * @author Gary Clayburg
 */
//@VaadinUI(path = "/console")
@VaadinUI(path = "/start")  // maps to e.g.: localhost:8080/console via application.properties
@Widgetset("com.garyclayburg.AppWidgetSet")
@Title("user console!")
public class VConsole extends UI implements UserChangeListener{
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(VConsole.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")  // IntelliJ confused by spring-boot wiring
    private AutoUserRepo autoUserRepo;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private AttributeService attributeService;
    private Map<String, Window> targetWindows;

    @Autowired
    private UserChangeController userChangeController;
    private Table userTable;
    private BeanContainer<String, GeneratedAttributesBean> attributesBeanContainer;

    public VConsole() {
        targetWindows = new HashMap<String, Window>();
    }

    protected void init(VaadinRequest vaadinRequest) {
        final Refresher refresher = new Refresher();
        refresher.addListener(new Refresher.RefreshListener() {
            @Override
            public void refresh(Refresher refresher) {
//                log.debug("refreshing UI...");
            }
        });
        addExtension(refresher);

        targetWindows = new HashMap<String, Window>();
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        setContent(layout);

        List<User> allUsers = autoUserRepo.findAll();

        BeanContainer<String, User> userBeanContainer = new BeanContainer<String, User>(User.class);
        userBeanContainer.setBeanIdProperty("id");
        User firstUser = null;
        if ( allUsers.size() >0) {
            firstUser = allUsers.get(0);
        }
        for (User user : allUsers) {
            userBeanContainer.addBean(user);
            userChangeController.addChangeListener(user,this);
        }
        userTable = createUserTable(userBeanContainer);
        final Table attributeTable = new Table();
        attributeTable.setSizeFull();
        attributeTable.setSelectable(true);
        attributeTable.setMultiSelect(false);
        attributeTable.setImmediate(true);

        attributesBeanContainer = new BeanContainer<String, GeneratedAttributesBean>(GeneratedAttributesBean.class);
        attributesBeanContainer.setBeanIdProperty("attributeName");
        populateItems(firstUser,attributesBeanContainer);

        attributeTable.setContainerDataSource(attributesBeanContainer);

        userTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                User selectedUser = (User) ((BeanItem) event.getItem()).getBean();
                refreshUserValues(selectedUser);
            }
        });

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSizeFull();
        splitPanel.setSplitPosition(150,Unit.PIXELS);
        splitPanel.setFirstComponent(userTable);
        splitPanel.setSecondComponent(attributeTable);

        layout.addComponent(splitPanel);
    }

    private void refreshUserValues(User selectedUser) {
        populateItems(selectedUser,attributesBeanContainer);

        Set<String> entitledTargets = attributeService.getEntitledTargets(selectedUser);
        for (String entitledTarget : entitledTargets) {
            populateTargetWindow(selectedUser,entitledTarget);

        }
    }

    @Override
    public void userChanged(User user) {
        log.info("user is changing: " + user.getFirstname());
        BeanContainer beanContainer = (BeanContainer) userTable.getContainerDataSource();
        BeanItem item = beanContainer.getItem(user.getId());
        if (item != null) {
            log.info("updating user");
            item.getItemProperty("firstname")
                    .setValue(user.getFirstname());
            item.getItemProperty("lastname")
                    .setValue(user.getLastname());
//            userTable.setImmediate(true);
//            userTable.refreshRowCache();
//            userTable.markAsDirty();
        }
        if (userTable.isSelected(user.getId())){
            refreshUserValues(user);
        }
    }

    private void populateTargetWindow(User selectedUser,final String entitledTarget) {
        Window window = targetWindows.get(entitledTarget);
        if (window == null){
            window = new Window(entitledTarget);
            targetWindows.put(entitledTarget,window);
            window.addCloseListener(new Window.CloseListener() {
                @Override
                public void windowClose(Window.CloseEvent e) {
                    targetWindows.remove(entitledTarget);
                }
            });
            UI.getCurrent().addWindow(window);
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
        final Collection[] selectedRows = new Collection[1];
        final Table userTable = new Table();
        userTable.setSizeFull();
        userTable.setSelectable(true);
        userTable.setMultiSelect(true);
        userTable.setImmediate(true);
        userTable.setContainerDataSource(userBeanContainer);
        userTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                Collection<?> itemPropertyIds = event.getItem()
                        .getItemPropertyIds();
                log.info("properties clicked: " + itemPropertyIds);
                log.info("multiple select? " + selectedRows[0]);
            }
        });
        userTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedRows[0] = (Collection) userTable.getValue();
                log.info("selected: " + selectedRows[0]);
            }
        });
        final Action myADaction =new Action("myAD window");
        userTable.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target,Object sender) {
                return new Action[] {myADaction};
            }

            @Override
            public void handleAction(Action action,Object sender,Object target) {
                Window multiWindow = new Window("multi-user myAD attributes");
                UI.getCurrent().addWindow(multiWindow);
                Label stuff = new Label("2 users here "+ selectedRows[0] + " " );
                multiWindow.setContent(stuff);
            }
        });
//        userTable.setVisibleColumns("firstname","lastname");
        return userTable;
    }
}
