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
import com.vaadin.data.util.BeanContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.UIScope;
import org.vaadin.spring.VaadinComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/12/14
 * Time: 11:50 AM
 *
 * @author Gary Clayburg
 */
@VaadinComponent
@UIScope
public class TargetWindows {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(TargetWindows.class);
    private Map<String, Window> targetWindows;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private AttributeService attributeService;

    public TargetWindows() {
        targetWindows = new HashMap<>();
    }

    void showTargetWindow(User selectedUser,final String entitledTarget) {
        Window window = targetWindows.get(entitledTarget);
        if (window == null) {  //only generate new window if user clicked on it - no new windows from policy update
            UI ui = UI.getCurrent();
            if (ui != null) {
                window = new Window(entitledTarget);
                window.setWidth("300px");
                targetWindows.put(entitledTarget,window);
                window.addCloseListener(new Window.CloseListener() {
                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        targetWindows.remove(entitledTarget);
                    }
                });
                ui.addWindow(window);
            } else{
                return;
            }
        }
        populateTarget(selectedUser,entitledTarget);
    }

    void refreshOpenTargets(User selectedUser) {
        for (String openWindowTargetName : targetWindows.keySet()) {
            populateTarget(selectedUser,openWindowTargetName);
        }
    }

    void populateTarget(User selectedUser,String entitledTarget){
        Window window = targetWindows.get(entitledTarget);
        if (window !=null) {
            UI ui = window.getUI();
            if (ui != null){
                VaadinSession session = ui.getSession();
                if (session !=null){
                    session.getLockInstance().lock();
                    try {
                        VerticalLayout windowContent = populateTargetWindowData(selectedUser,entitledTarget);
                        window.setContent(windowContent);

                    } finally{
                        session.getLockInstance().unlock();
                    }
                }
            }
        }
    }

    private VerticalLayout populateTargetWindowData(User selectedUser,String entitledTarget) {
        VerticalLayout windowContent = new VerticalLayout();
        windowContent.setMargin(false);

        final Table attributeTargetTable = new Table();
        attributeTargetTable.setSizeFull();
        attributeTargetTable.setSelectable(true);
        attributeTargetTable.setMultiSelect(false);
        attributeTargetTable.setImmediate(true);
        final BeanContainer<String, GeneratedAttributesBean> attributesBeanContainer =
                new BeanContainer<>(GeneratedAttributesBean.class);
        attributesBeanContainer.setBeanIdProperty("attributeName");

        populateItems(selectedUser,attributesBeanContainer,entitledTarget);

        attributeTargetTable.setContainerDataSource(attributesBeanContainer);

        windowContent.addComponent(attributeTargetTable);
        return windowContent;
    }

    private void populateItems(User firstUser,BeanContainer<String, GeneratedAttributesBean> generatedAttributesBeanContainer,String targetName) {
        List<GeneratedAttributesBean> generatedAttributes =
                attributeService.getGeneratedAttributesBean(firstUser,targetName);
        generatedAttributesBeanContainer.removeAllItems();
        for (GeneratedAttributesBean generatedAttribute : generatedAttributes) {
            generatedAttributesBeanContainer.addBean(generatedAttribute);
        }
    }
}
