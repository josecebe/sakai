package org.sakaiproject.content.entityproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.sakaiproject.component.api.ServerConfigurationService;
import static org.sakaiproject.content.tool.FilePickerAction.STATE_GOOGLEDRIVE_CHILDREN;
import static org.sakaiproject.content.tool.FilePickerAction.STATE_GOOGLEDRIVE_ITEMS;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.googledrive.model.GoogleDriveItem;
import org.sakaiproject.googledrive.service.GoogleDriveService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.user.api.UserDirectoryService;

public class AjaxContentEntityProvider extends AbstractEntityProvider implements EntityProvider, AutoRegisterEntityProvider, ActionsExecutable, Outputable, Describeable {

    public final static String ENTITY_PREFIX = "contentajax";

    @Getter @Setter private ServerConfigurationService serverConfigurationService;
    @Getter @Setter private GoogleDriveService googleDriveService;
    @Getter @Setter private UserDirectoryService userDirectoryService;
    @Getter @Setter private SessionManager sessionManager;

    @Override
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    @Override
    public String[] getHandledOutputFormats() {
        return new String[] { Formats.JSON };
    }

    @EntityCustomAction(action = "doNavigateGoogleDrive", viewKey = EntityView.VIEW_LIST)
    public JSONArray handleChatData(EntityReference view, Map<String,Object> params) {
        boolean googledriveOn = serverConfigurationService.getBoolean(GoogleDriveService.GOOGLEDRIVE_ENABLED, Boolean.FALSE);
        String googledriveCollectionId = (String) params.get("googledriveCollectionId");
        JSONArray json = new JSONArray();

        if (googledriveOn){
            List<GoogleDriveItem> children;

            if (googledriveCollectionId == null) {
                System.out.println("------------1------------");
                //children = googleDriveService.getDriveRootItems(userDirectoryService.getCurrentUser().getId());
                children = googleDriveService.getDriveRootItems("16b4ba7b-4f8e-4638-aea1-cda106a3070a");

            } else {
                System.out.println("------------2------------");
                int depth = 0;
                if (params.get("googledriveCollectionDepth") != null) depth = Integer.parseInt(params.get("googledriveCollectionDepth").toString());
                //children = googleDriveService.getDriveChildrenItems(userDirectoryService.getCurrentUser().getId(), googledriveCollectionId, depth);
                children = googleDriveService.getDriveChildrenItems("16b4ba7b-4f8e-4638-aea1-cda106a3070a", googledriveCollectionId, depth);
            }

            for (GoogleDriveItem child : children) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", child.getGoogleDriveItemId());
                jsonObj.put("text", child.getName());
                JSONObject jsonState = new JSONObject();
                jsonState.put("opened", child.isExpanded());
                jsonState.put("selected", child.isExpanded());
                jsonObj.put("state", jsonState);
                if (child.isFolder()) {
                    jsonObj.put("children", true);
                }
                json.add(jsonObj);
            }
        }
        JSONArray json2 = new JSONArray();
        json2.add(json);
        return json2;
    }
}
