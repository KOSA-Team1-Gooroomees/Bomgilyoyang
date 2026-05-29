package com.gooroomees.neulbomgil_backend.domain.map.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VWorldResponse {

    private ResponseData response;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        private String status;
        private RecordInfo record;
        private PageInfo page;
        private ResultData result;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RecordInfo {
        private String total;
        private String current;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PageInfo {
        private String total;
        private String current;
        private String size;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultData {
        private FeatureCollection featureCollection;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeatureCollection {
        private List<Feature> features;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private String id;
        private Geometry geometry;
        private Properties properties;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Geometry {
        private String type;
        private List<Double> coordinates; // [경도, 위도] 순서
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        @JsonProperty("fac_nam")
        private String facilityName;

        @JsonProperty("fac_tel")
        private String facilityTel;

        @JsonProperty("cat_nam")
        private String categoryName;

        @JsonProperty("fac_o_add")
        private String oldAddress;

        @JsonProperty("fac_n_add")
        private String newAddress;
    }
}