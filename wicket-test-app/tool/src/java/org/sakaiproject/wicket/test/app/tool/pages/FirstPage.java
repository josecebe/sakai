package org.sakaiproject.wicket.test.app.tool.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.sakaiproject.wicket.test.app.api.model.GradingInfo;

public class FirstPage extends BasePage {
    Set<GradingInfo> gradings = new HashSet<GradingInfo>();

    public FirstPage() {
        List<IColumn> columns = new ArrayList<IColumn>();
        columns.add(new PropertyColumn(new Model("Student Name"), "student.name", "student.name"));

        Set<GradingInfo> gradings = gradingsService.getGradingsFromSite("x", null, null);
        for (GradingInfo grading : gradings) {
            System.out.println("Student Name: " + grading.getStudentName());
            System.out.println("Student email: " + grading.getStudentId());
        }

        DataTable dataTable = new DataTable("datatable", columns, new GradingsDataProvider(), 20);
        add(dataTable);
    }

    private class GradingsDataProvider extends SortableDataProvider {
        @Override
        public Iterator iterator(long first, long count) {
            // In this example the whole list gets copied, sorted and sliced; in real applications typically your database would deliver a sorted and limited list

            // Get the data
            List<GradingInfo> list = new ArrayList<GradingInfo>();
            list.addAll(gradings);

            // Sort the data
            //Collections.sort(list, comparator);

            // Return the data for the current page - this can be determined only after sorting
            return list.subList((int) first, (int) (first + count)).iterator();
        }

        @Override
        public long size() {
            return gradings.size();
        }

        @Override
        public IModel model(Object object) {
            return new AbstractReadOnlyModel<GradingInfo>() {
                @Override
                public GradingInfo getObject() {
                    return (GradingInfo) object;
                }
            };
        }
    }
}
