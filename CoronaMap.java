package covidMap;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Google.*;

import java.util.List;


import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;

import java.util.ArrayList;
import java.util.HashMap;


import de.fhpotsdam.unfolding.marker.Marker;
public class CoronaMap extends PApplet {
	private String coronafile = "https://covid.ourworldindata.org/data/owid-covid-data.csv";
	//private String coronafile = "owid-covid-data.csv";
	private List<Marker> countryMarkers;
	private HashMap<String,Integer> coronaMap;
	UnfoldingMap map ;
	
	private List<Marker> cityMarkers;
	private String cityFile = "city-data.json";
	
	private static final long serialVersionUID = 1L;
	
	private CommonMarker lastSelected;
	public void setup() {
		size(900,700,OPENGL);
		map =  new UnfoldingMap(this, 200, 50, 650, 600, new Microsoft.HybridProvider());
		
		coronaMap = ParseFeed.loadCoronaDetailsFromCSV(this, coronafile);
		
		List<Feature> countryFeature = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countryFeature);
		
		
		colorMap();
		MapUtils.createDefaultEventDispatcher(this, map);
		
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
		
		map.addMarkers(countryMarkers);
		map.addMarkers(cityMarkers);
		
	}
	public void draw() {
		background(0,0,0);
		map.draw();
		addKey();
	}
	
	public void colorMap() {
		for(Marker m : countryMarkers) {
			String countryId = m.getId();
			
			if(coronaMap.containsKey(countryId)) {
				
				int activeCases = coronaMap.get(countryId);
				
				if(activeCases > 400000) {
					int shade = color(0,0,153);
					m.setColor(shade);
				}
				else if(activeCases > 100000) {
					int shade = color(102,102,255);
					m.setColor(shade);
				}
				
				else if(activeCases > 10000) {
					int shade = color(102,178,255);
					m.setColor(shade);
				}
				else if(activeCases > 1){
					m.setColor(color(153,255,255));
				}
				
				else {
					m.setColor(color(255,255,255));
				}
			}
			else {
				m.setColor(color(210,209,0));
			}
		}
	}
	
	public void mouseMoved()
	{
	
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		
		selectMarkerIfHover(cityMarkers);
		
	}
	
	private void selectMarkerIfHover(List<Marker> markers)
	{
		
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	public void addKey() {
		fill(255, 250, 240);
		rect(20,50,170,300);
		
		fill(0,0,153);
		rect(35,85,15,15);
		fill(102,102,255);
		rect(35,120,15,15);
		
		fill(102,178,255);
		rect(35,155,15,15);
		fill(153,255,255);
		rect(35,190,15,15);
		fill(255,255,255);
		rect(35,225,15,15);
		fill(210,209,0);
		rect(35,260,15,15);
		fill(150, 30, 30);
		triangle(40,310,33,325,47,325);
		
		fill(0,0,0);
		textAlign(LEFT, CENTER);
		textSize(15);
		text("Corona Map Key",35,65);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("> 400 Thousand", 65,90);
		text("> 100 Thousand",65,125);
		text("> 10 Thousand",65,160);
		text("Atleast 1",65,195);
		text("No cases",65,230);
		text("Data not aval.",65,265);
		text("~~~~~~~~~~~~~~~~~~~~~~~~~~",35,290);
		text("~~~~~~~~~~~~~~~~~~~~~~~~~~",35,295);
		text("City Marker",65,315);
	}
	
}
