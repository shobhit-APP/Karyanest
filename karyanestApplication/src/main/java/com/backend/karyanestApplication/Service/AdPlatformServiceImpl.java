package com.backend.karyanestApplication.Service;

import com.backend.karyanestApplication.Interface.AdPlatformService;
import com.backend.karyanestApplication.Model.AdPlatforms;
import com.backend.karyanestApplication.Repositry.AdPlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdPlatformServiceImpl implements AdPlatformService {

    @Autowired
    private AdPlatformRepository adPlatformRepository;

    @Override
    public List<AdPlatforms> getAllPlatforms() {
        return adPlatformRepository.findAll();
    }
}
