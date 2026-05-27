package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.ObjectModel;

import java.util.List;

public interface IObjectServicePort {

    void saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();
}