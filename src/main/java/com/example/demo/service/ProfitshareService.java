package com.example.demo.service;
import com.example.demo.dto.OfferDTO;
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

    @Value("${profitshare.data.path}")
    private String dataPath;

    public List<OfferDTO> getOffers(
            String keyword,
            int page,
            int size
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // citește JSON -> array
        OfferDTO[] offersArray = mapper.readValue(
                new File(dataPath),
                OfferDTO[].class
        );

        // transformă în listă
        List<OfferDTO> offers = new ArrayList<>(
                Arrays.asList(offersArray)
        );

        // filtrare după keyword
        if (keyword != null && !keyword.isBlank()) {

            String searchTerm = keyword.toLowerCase();

            offers = offers.stream()
                    .filter(offer ->
                            offer.getName() != null &&
                                    offer.getName()
                                            .toLowerCase()
                                            .contains(searchTerm)
                    )
                    .collect(Collectors.toList());
        }

        // calcul paginare
        int start = page * size;

        if (start >= offers.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, offers.size());

        return offers.subList(start, end);
    }
}
