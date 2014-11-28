package com.hooapps.pca.cvilleart.artfinder.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PCAResponse {

    @SerializedName("results")
    public List<PCAObject> results;

    class PCAObject {
        @SerializedName("Description")
        public String description;

        @SerializedName("createdAt")
        public String createdAtString;

        @SerializedName("updatedAt")
        public String updatedAtString;

        @SerializedName("objectId")
        public String parseObjectId;
    }

    public String getDescription() {
        if (results.isEmpty()) {
            return "Piedmont Council for the Arts (PCA)";
        }
        return results.get(0).description;
    }
}
