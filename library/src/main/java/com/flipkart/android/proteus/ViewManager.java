/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
 *
 * Copyright (c) 2017 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.flipkart.android.proteus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.flipkart.android.proteus.toolbox.BoundAttribute;
import com.flipkart.android.proteus.toolbox.Result;
import com.flipkart.android.proteus.toolbox.Scope;
import com.flipkart.android.proteus.toolbox.Utils;
import com.flipkart.android.proteus.value.Layout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * ViewManager
 *
 * @author aditya.sharat
 */
public class ViewManager implements ProteusViewManager {

    private static final String TAG = "ViewManager";

    @NonNull
    private final ProteusContext context;

    @NonNull
    private final View view;

    @NonNull
    private final Layout layout;

    @NonNull
    private final Scope scope;

    @NonNull
    private final ViewTypeParser parser;

    @Nullable
    private String dataPathForChildren;

    @Nullable
    private Layout childLayout;

    private BoundAttribute[] boundAttributes;

    public ViewManager(@NonNull ProteusContext context, @NonNull ViewTypeParser parser,
                       @NonNull View view, @NonNull Layout layout, @NonNull Scope scope) {
        this.context = context;
        this.parser = parser;
        this.view = view;
        this.layout = layout;
        this.scope = scope;
    }

    @Override
    public void update(@Nullable JsonObject data) {
        // update the data context so all child views can refer to new data
        if (data != null) {
            updateDataContext(data);
        }

        // update the boundAttributes of this view
        if (this.boundAttributes != null) {
            for (BoundAttribute boundAttribute : this.boundAttributes) {
                this.handleBinding(boundAttribute);
            }
        }

        // update the child views
        if (view instanceof ViewGroup) {
            if (dataPathForChildren != null) {
                updateChildrenFromData();
            } else {
                ViewGroup parent = (ViewGroup) view;
                View child;
                int childCount = parent.getChildCount();

                for (int index = 0; index < childCount; index++) {
                    child = parent.getChildAt(index);
                    if (child instanceof ProteusView) {
                        ((ProteusView) child).getViewManager().update(scope.getData());
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    public ProteusContext getContext() {
        return this.context;
    }

    @NonNull
    @Override
    public ViewTypeParser getParser() {
        return parser;
    }

    @NonNull
    @Override
    public Layout getLayout() {
        return this.layout;
    }

    @NonNull
    @Override
    public Scope getScope() {
        return scope;
    }

    @Nullable
    @Override
    public View findViewById(@NonNull String id) {
        return view.findViewById(context.getInflater().getUniqueViewId(id));
    }

    @Nullable
    @Override
    public Layout getChildLayout() {
        return childLayout;
    }

    @Override
    public void setChildLayout(@Nullable Layout layout) {
        this.childLayout = layout;
    }

    @Nullable
    @Override
    public String getDataPathForChildren() {
        return dataPathForChildren;
    }

    @Override
    public void setDataPathForChildren(@Nullable String dataPathForChildren) {
        this.dataPathForChildren = dataPathForChildren;
    }

    @Override
    public void destroy() {
        childLayout = null;
        dataPathForChildren = null;
        boundAttributes = null;
    }

    private void updateDataContext(JsonObject data) {
        if (scope.isClone()) {
            scope.setData(data);
        } else {
            scope.updateDataContext(data);
        }
    }

    private void updateChildrenFromData() {
        JsonArray dataList = new JsonArray();
        ViewGroup parent = ((ViewGroup) view);
        Result result = Utils.readJson(dataPathForChildren, scope.getData(), scope.getIndex());
        if (result.isSuccess() && null != result.element && result.element.isJsonArray()) {
            dataList = result.element.getAsJsonArray();
        }

        int childCount = parent.getChildCount();
        View child;

        if (childCount > dataList.size()) {
            while (childCount > dataList.size()) {
                childCount--;
                child = parent.getChildAt(childCount);
                if (child instanceof ProteusView) {
                    ((ProteusView) child).getViewManager().destroy();
                }
                parent.removeViewAt(childCount);
            }
        }

        JsonObject data = scope.getData();
        ProteusView childView;
        childCount = parent.getChildCount();

        for (int index = 0; index < dataList.size(); index++) {
            if (index < childCount) {
                child = parent.getChildAt(index);
                if (child instanceof ProteusView) {
                    ((ProteusView) child).getViewManager().update(data);
                }
            } else if (childLayout != null) {
                childView = context.getInflater().inflate(getLayout(), data, parent, scope.getIndex());
                parser.addView((ProteusView) view, childView);
            }
        }
    }

    private void handleBinding(BoundAttribute boundAttribute) {
        //noinspection unchecked
        parser.handleAttribute(view, boundAttribute.attributeId, boundAttribute.attributeValue);
    }
}