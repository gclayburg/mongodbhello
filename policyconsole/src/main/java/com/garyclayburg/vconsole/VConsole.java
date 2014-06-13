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
import com.garyclayburg.attributes.PolicyChangeController;
import com.garyclayburg.attributes.PolicyChangeListener;
import com.garyclayburg.persistence.UserChangeController;
import com.garyclayburg.persistence.UserChangeListener;
import com.garyclayburg.persistence.domain.QUser;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import com.github.wolfie.refresher.Refresher;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.*;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.spring.VaadinUI;

import java.io.PrintWriter;
import java.io.StringWriter;
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
@Title("policy console")
@Theme("dashboard")
public class VConsole extends UI implements UserChangeListener {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(VConsole.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")  // IntelliJ confused by spring-boot wiring
    private AutoUserRepo autoUserRepo;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private AttributeService attributeService;

    @Autowired
    private UserChangeController userChangeController;

    private Table userTable;
    private BeanContainer<String, GeneratedAttributesBean> attributesBeanContainer;
    @Qualifier("policyChangeController")
    @Autowired
    private PolicyChangeController policyChangeController;
    private final Window exceptionWindow;
    private final Window notifications;
    private Button notify;
    private HorizontalLayout topLayout;
    private Map<String, Throwable> scriptErrors;
    private final Object scriptErrorsLock;

    @Autowired
    private TargetWindows targetWindows;
    private Table attributeTable;
    private User firstUser;

    public VConsole() {
        exceptionWindow = new Window("policy exception");
        notifications = new Window("Policy errors");
        scriptErrorsLock = new Object();
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

        addStyleName("dashboard-view");
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);


        List<User> searchedUsers;

        TextField searchField = new TextField();
        searchField.setInputPrompt("name or jql query");
        searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);
        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                QUser qUser = new QUser("user");
                Iterable<User> searchedUsers = autoUserRepo.findAll(qUser.firstname.eq(event.getText()));
                updateUserList(searchedUsers);
            }
        });


//        searchedUsers = autoUserRepo.findAll();

        createExceptionWindow("no errors yet...");

        final User finalFirstUser = firstUser;
        policyChangeController.addChangeListener(new PolicyChangeListener() {
            @Override
            public void policyChanged() {
                log.info("policy is changing");
                BeanContainer beanContainer = (BeanContainer) userTable.getContainerDataSource();
                List itemIds = beanContainer.getItemIds();
                boolean refreshedSelected = false;
                for (Object itemId : itemIds) {
                    String id = (String) itemId;
                    BeanItem item = beanContainer.getItem(itemId);
                    log.debug("refreshing user id: " + id);
                    User user = (User) item.getBean();
                    log.debug("refreshing user: " + user.getFirstname());
                    if (userTable.isSelected(itemId)) {
                        refreshUserValues(user);
                        refreshedSelected = true;
                    }
                }
                if (!refreshedSelected && finalFirstUser != null){
                    refreshUserValues(finalFirstUser);
                }
                populatePolicyExceptionList();
            }

            @Override
            public void policyException(Throwable e) {
                populatePolicyExceptionList();
            }
        });
        attributeTable = new Table();
        attributeTable.setSizeFull();
        attributeTable.setSelectable(true);
        attributeTable.setMultiSelect(false);
        attributeTable.setImmediate(true);

        attributesBeanContainer = new BeanContainer<>(GeneratedAttributesBean.class);
        attributesBeanContainer.setBeanIdProperty("attributeName");

        attributeTable.setContainerDataSource(attributesBeanContainer);

        BeanContainer<String, User> userBeanContainer = new BeanContainer<>(User.class);
        userBeanContainer.setBeanIdProperty("id");
        userTable = createUserTable(userBeanContainer);
        userTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                User selectedUser = (User) ((BeanItem) event.getItem()).getBean();
                refreshUserValues(selectedUser);
                populatePolicyExceptionList();
            }
        });
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSizeFull();
        splitPanel.setSplitPosition(150,Unit.PIXELS);
        splitPanel.setFirstComponent(userTable);
        splitPanel.setSecondComponent(attributeTable);

//        MenuBar menuBar = createMenu();

//        layout.addComponent(menuBar);

        HorizontalLayout top = createTop();
        populatePolicyExceptionList();
        layout.addComponent(top);


        layout.addComponent(searchField);
        layout.addComponent(splitPanel);
        populateItems(firstUser,attributesBeanContainer);

    }

    private void updateUserList(Iterable<User> searchedUsers) {
        BeanContainer<String, User> userBeanContainer = new BeanContainer<>(User.class);
        userBeanContainer.setBeanIdProperty("id");
        firstUser = null;
        boolean markedFirst = false;
        for (User user : searchedUsers) {
            if (!markedFirst){
                markedFirst=true;
                firstUser = user;
            }
            userBeanContainer.addBean(user);
            userChangeController.addChangeListener(user,this); //todo: implement remove change controller...
        }
        userTable.setContainerDataSource(userBeanContainer);
        userTable.setVisibleColumns("firstname","lastname");
        if (firstUser !=null) {
            userTable.select(userBeanContainer.getIdByIndex(0));
            populateItems(firstUser,attributesBeanContainer);
        }
    }

    private void populatePolicyExceptionList() {
        synchronized(scriptErrorsLock) {
            scriptErrors = attributeService.getScriptErrors();
            if (scriptErrors.size() > 0) {
                notify.addStyleName("unread");
                notify.setDescription("policy error(s) detected: " + scriptErrors.size());
                notify.setCaption(String.valueOf(scriptErrors.size()));
            } else {
                notify.removeStyleName("unread");
                notify.setDescription("No errors in policy");
            }
        }
    }

    private void buildNotifications(Button.ClickEvent event) {
        VerticalLayout l = new VerticalLayout();
        l.setMargin(true);
        l.setSpacing(true);
        notifications.setContent(l);
        notifications.setWidth("1000px");
        notifications.setHeight("500px");
        notifications.addStyleName("notifications");
        notifications.setClosable(false);
        notifications.setResizable(true);
        notifications.setDraggable(false);
        notifications.setPositionX(event.getClientX() - event.getRelativeX() -40 ); /* magic number used to adjust right edge of window so that it draws within the browser window.  This number is related to css values in dashboard.scss and relative positioning of the bell icon in topLayout */
        notifications.setPositionY(event.getClientY() - event.getRelativeY());
        notifications.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);

        synchronized(scriptErrorsLock) {
            log.debug("checking for new policy errors {}",scriptErrors.size());
            Label messageLabel;
            if (scriptErrors.size() >0) {
                for (String absolutePath : scriptErrors.keySet()) {
                    Throwable scriptException = scriptErrors.get(absolutePath);
                    String message = scriptException.getMessage();
                    if (message != null) {
                        String formattedMessage = MessageHelper.scrubMessage(message);
                        messageLabel = new Label("<hr>" + formattedMessage,ContentMode.HTML);
                    } else {
                        StringWriter errors = new StringWriter();
                        scriptException.printStackTrace(new PrintWriter(errors));
                        String stackTrace = errors.toString();
                        stackTrace = MessageHelper.scrubMessage(stackTrace);
                        messageLabel = new Label("<hr>" + stackTrace,ContentMode.HTML);
                    }
                    messageLabel.setStyleName(Runo.LABEL_SMALL);
                    l.addComponent(messageLabel);
                }
            } else{
                messageLabel = new Label("<hr>none<br>"  ,ContentMode.HTML);
                messageLabel.setStyleName(Runo.LABEL_SMALL);
                l.addComponent(messageLabel);
            }
        }
    }

    private HorizontalLayout createTop() {
        topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.setSpacing(true);
        topLayout.addStyleName("toolbar");
        final Label title = new Label("Policy console");
        title.setSizeUndefined();
        title.addStyleName("h1");
        topLayout.addComponent(title);
        topLayout.setComponentAlignment(title,Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(title,1);

        createNotifyButton();
        topLayout.addComponent(notify);
        topLayout.setComponentAlignment(notify,Alignment.MIDDLE_LEFT);
        return topLayout;
    }

    private void createNotifyButton() {

        notify = new Button();
        notify.setDescription("No errors in policy");
        // notify.addStyleName("borderless");
        notify.addStyleName("notifications");
        notify.addStyleName("unread");
        notify.addStyleName("icon-only");
        notify.addStyleName("icon-bell");
        notify.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.info("clicked on notifications");
                if (notifications.getUI() != null){
                    notifications.close();
                    log.debug("closed notifications");
                } else {
                    buildNotifications(event);
                    getUI().addWindow(notifications);
                    notifications.focus();
                    ((VerticalLayout) getUI().getContent()).addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
                        @Override
                        public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                            notifications.close();
                            ((VerticalLayout) getUI().getContent()).removeLayoutClickListener(this);
                        }
                    });
                }

            }
        });

    }

    private void createExceptionWindow(String message) {
        populateExceptionMessage(message,false);
        exceptionWindow.setClosable(false);
//        exceptionWindow.setWidth("80em");
        UI.getCurrent()
                .addWindow(exceptionWindow);
    }

    private void populateExceptionMessage(String message,boolean visible) {
        VerticalLayout windowContent = new VerticalLayout();
        windowContent.setMargin(true);

        Label messageLabel = new Label(message,ContentMode.PREFORMATTED);
        messageLabel.setStyleName(Runo.LABEL_SMALL);
//        messageLabel.setWidth("120em");
        windowContent.addComponent(messageLabel);
        Button closeButton = new Button("Close");
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                exceptionWindow.setVisible(false);
            }
        });

        windowContent.addComponent(closeButton);
        UI ui = exceptionWindow.getUI();
        if (ui != null) {
            VaadinSession session = ui.getSession();
            if (session != null) {
                session.getLockInstance()
                        .lock();
                try {
                    exceptionWindow.setContent(windowContent);
                    exceptionWindow.setVisible(visible);
                } finally {
                    session.getLockInstance()
                            .unlock();
                }
            } else {
                exceptionWindow.setContent(windowContent);
                exceptionWindow.setVisible(visible);
            }
        } else {
            exceptionWindow.setContent(windowContent);
            exceptionWindow.setVisible(visible);
        }
    }

    private void refreshUserValues(User selectedUser) {
        populateItems(selectedUser,attributesBeanContainer);

        targetWindows.refreshOpenTargets(selectedUser);
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
        if (userTable.isSelected(user.getId())) {
            refreshUserValues(user);
            populatePolicyExceptionList();
        }
    }



    private void populateItems(User firstUser,BeanContainer<String, GeneratedAttributesBean> generatedAttributesBeanContainer) {
        if (firstUser !=null) {
            List<GeneratedAttributesBean> generatedAttributes = attributeService.getGeneratedAttributesBean(firstUser);

            UI ui = attributeTable.getUI();
            if (ui != null) {
                VaadinSession session = ui.getSession();
                if (session != null) {
                    session.getLockInstance()
                            .lock();
                    try {
                        generatedAttributesBeanContainer.removeAllItems();
                        for (GeneratedAttributesBean generatedAttribute : generatedAttributes) {
                            generatedAttributesBeanContainer.addBean(generatedAttribute);
                        }
                    } finally {
                        session.getLockInstance()
                                .unlock();
                    }
                }
            }
        }
    }


    private Table createUserTable(BeanContainer<String, User> userBeanContainer) {
        final Collection[] selectedRows = new Collection[1];
        final Table userTable = new Table();
        userTable.setSizeFull();
        userTable.setSelectable(true);
        userTable.setMultiSelect(false);
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
//                selectedRows[0] = (Collection) userTable.getValue();
                log.info("selected: " + userTable.getValue());
            }
        });




        userTable.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target,Object sender) {
                Table selectedUserTable = (Table) sender;
                Item item = selectedUserTable.getItem(selectedUserTable.getValue());
                Action[] actions = new Action[0];
                if (item instanceof BeanItem) {
                    log.debug("create right-click menu items");
                    User user = (User) ((BeanItem) item).getBean();
                    if (user !=null) {
                        Set<String> allEntitledTargets = attributeService.getEntitledTargets(user);
                        Set<Action> entitledTargetActions = new HashSet<>();

                        for (String targetName : allEntitledTargets) {
                            final Action action = new Action(targetName);
                            entitledTargetActions.add(action);
                        }
                        actions = entitledTargetActions.toArray(new Action[entitledTargetActions.size()]);
                    }
                } else {
                    log.debug("Cannot create right-click menu items");
                }

                return actions;
            }

            @Override
            public void handleAction(Action action,Object sender,Object target) {
                if (action != null) {
                    Table selectedUserTable = (Table) sender;
                    selectedUserTable.getValue();
                    Item item = selectedUserTable.getItem(selectedUserTable.getValue());

                    if (item instanceof BeanItem){
                        log.debug("create target window");
                        User user = (User) ((BeanItem) item).getBean();
                        targetWindows.showTargetWindow(user,action.getCaption());
                    } else{
                        log.debug("Cannot create window");
                    }
                } else {
                    Window multiWindow = new Window("multi-user myAD attributes");
                    UI.getCurrent()
                            .addWindow(multiWindow);
                    Label stuff = new Label("2 users here " + selectedRows[0] + " ");
                    multiWindow.setContent(stuff);
                }
            }
        });
        userTable.setVisibleColumns("firstname","lastname");
        return userTable;
    }
}
