package com.example.demo.service;
import com.example.demo.dto.OfferDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
@Service

public class ProfitshareService {

    private List<OfferDTO> offers;

    @Value("${profitshare.data.path}")
    //@Value("${profitshare.data.path}")
    private String dataPath;

    @PostConstruct
    public void init() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        OfferDTO[] offersArray = mapper.readValue(
                new File(dataPath),
                OfferDTO[].class
        );
        offers=Arrays.asList(offersArray);
    }

    public List<OfferDTO> getOffers(
            String keyword,
            int page,
            int size)
    {
        List<OfferDTO> filtered=offers;

        // filtrare după keyword
        if (keyword != null && !keyword.isBlank()) {

            filtered=offers.stream()
                    .filter(o->
                            o.getName()
                                    .toLowerCase()
                                    .contains(keyword.toLowerCase()))
                    .toList();
        }

        // calcul paginare
        int start = page * size;

        if (start >= filtered.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, filtered.size());

        return filtered.subList(start, end);
    }
}
