package com.kkhosla.njtransitbuslive;

/**
 * Created by kkhosla2 on 7/15/2016.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/bus")
public class WebhookEventsController {

    private static Logger LOG = LoggerFactory.getLogger(WebhookEventsController.class);

    @RequestMapping(path = "/{busNumber}/{stopNumber}", method=RequestMethod.GET)
    public @ResponseBody
    String receiveEvent(@PathVariable("busNumber") String busNumber,
                        @PathVariable("stopNumber") String stopNumber) throws IOException {

        String njTransitBusUrl = "http://mybusnow.njtransit.com/bustime/wireless/html/eta.jsp?route=164&direction=Midland+Park&showAllBusses=on&id=" + stopNumber;
        URL url = new URL(njTransitBusUrl);
        URLConnection urlConnection = url.openConnection();
        InputStreamReader urlReader = new InputStreamReader(urlConnection.getInputStream());
        String busDepartureInMins = executeGet(urlReader, busNumber);
        if(org.springframework.util.StringUtils.isEmpty(busDepartureInMins)) {
            return "Bus " + busNumber + " has no upcoming departues";
        }
        if(busDepartureInMins.contains("DELAY")) {
            return "Bus " + busNumber + " is delayed. No departure information available";
        }
        return ("The next " + busNumber + " Bus will depart in " + busDepartureInMins + " minutes");
    }

    public static String executeGet(Reader in1, String busNumber) throws IOException {

        BufferedReader in = new BufferedReader(in1);
        String inputLine;
        boolean foundMatchingBusNumberTagHeader = false;
        boolean foundMatchingBusNumberTag = false;
        String busKey = null;
        String busValue;
        Map<String, String> busNumberAndTimeMap = new HashMap();
        while ((inputLine = in.readLine()) != null) {
            if(!foundMatchingBusNumberTagHeader && inputLine.contains(busNumber) && !inputLine.contains("<b>")) {
                busKey = inputLine.replaceAll("To ", "").replaceAll("&nbsp;", "").trim();
                System.out.println(busKey);

                foundMatchingBusNumberTag = true;
                continue;
            }

            if(foundMatchingBusNumberTag && inputLine.contains("<font size=")) {
                foundMatchingBusNumberTagHeader = true;
                foundMatchingBusNumberTag = false;
                continue;
            }
            if(foundMatchingBusNumberTagHeader) {
                busValue = inputLine.replace("<b>", "").replaceAll("&nbsp;MIN</b>", "").replaceAll("</b>", "").trim();
                return busValue;
                //System.out.println(busValue);
                //busNumberAndTimeMap.put(busKey, busValue);
                //foundMatchingBusNumberTagHeader = false;
            }
        }
        in.close();
        return null;
    }
}
