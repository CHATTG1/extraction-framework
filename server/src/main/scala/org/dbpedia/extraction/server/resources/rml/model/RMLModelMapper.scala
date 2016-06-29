package org.dbpedia.extraction.server.resources.rml.model

import java.util.Date

import org.apache.jena.rdf.model.Resource
import org.dbpedia.extraction.mappings.{ConditionalMapping, GeoCoordinatesMapping, IntermediateNodeMapping, _}
import org.dbpedia.extraction.ontology.OntologyProperty
import org.dbpedia.extraction.ontology.datatypes.Datatype

/**
  * Class that adds rml mappings to a ModelWrapper
  */
class RMLModelMapper(modelWrapper: RMLModelWrapper) {

  //TODO: refactor this into seperate classes maybe?

  def addSimplePropertyMapping(mapping: SimplePropertyMapping) =
  {
    val uniqueString = baseName("")
    addSimplePropertyMappingToTriplesMap(mapping, modelWrapper.triplesMap, uniqueString)
  }

  private def addSimplePropertyMappingToTriplesMap(mapping: SimplePropertyMapping, triplesMap: Resource, uri: String) =
  {

    //create predicate object map
    val predicateObjectMap = modelWrapper.addPredicateObjectMap(uri + "SimplePropertyMapping/" + mapping.ontologyProperty.name + "/" + mapping.templateProperty)

    //add dcterms type to predicate map
    modelWrapper.addPropertyAsPropertyToResource(predicateObjectMap, Prefixes("dcterms") + "type", Prefixes("dbf") + "simplePropertyMapping" )

    //add predicate to predicate object map
    modelWrapper.addPropertyAsPropertyToResource(predicateObjectMap, Prefixes("rr") + "predicate", mapping.ontologyProperty.uri)

    //add object map with rml reference
    val objectMap = modelWrapper.addBlankNode()
    modelWrapper.addResourceAsPropertyToResource(predicateObjectMap, Prefixes("rr") + "objectMap", objectMap)
    modelWrapper.addLiteralAsPropertyToResource(objectMap, Prefixes("rml") + "reference", mapping.templateProperty)

    //add unit if present
    if(mapping.unit != null) addUnitToPredicateObjectMap(predicateObjectMap, mapping.unit)

    //add predicate object map to triples map
    modelWrapper.addPredicateObjectMapUriToTriplesMap(uri + "SimplePropertyMapping/" + mapping.ontologyProperty.name + "/" + mapping.templateProperty, triplesMap)

  }




  def addCalculateMapping(mapping: CalculateMapping) =
  {
    //TODO: implement
    println("Calculate Mappings not supported")
  }





  def addCombineDateMapping(mapping: CombineDateMapping) =
  {
    //TODO: implement
    println("Combine Date Mappings not supported")
  }





  def addDateIntervalMapping(mapping: DateIntervalMapping) =
  {
    val uniqueString = baseName("")
    addDateIntervalMappingToTriplesMap(mapping, modelWrapper.triplesMap, uniqueString)
  }

  def addDateIntervalMappingToTriplesMap(mapping: DateIntervalMapping, triplesMap : Resource, uri: String) =
  {
    addStartDateToDateIntervalMapping(uri, mapping, triplesMap)
    addEndDateToDateIntervalMapping(uri, mapping, triplesMap)
  }

  private def addStartDateToDateIntervalMapping(uri: String, mapping: DateIntervalMapping, triplesMap: Resource) =
  {
    addDateToDateIntervalMapping(uri, mapping, triplesMap, "start")
  }

  private def addEndDateToDateIntervalMapping(uri: String, mapping: DateIntervalMapping, triplesMap: Resource) =
  {
    addDateToDateIntervalMapping(uri, mapping, triplesMap, "end")
  }

  private def addDateToDateIntervalMapping(uri: String, mapping: DateIntervalMapping, triplesMap: Resource, endOrStart: String) =
  {

    var dateOntologyProperty: OntologyProperty  = null
    if(endOrStart == "start") {
      dateOntologyProperty = mapping.startDateOntologyProperty
    } else {
      dateOntologyProperty = mapping.endDateOntologyProperty
    }

    //create predicate object map for date
    val uniqueString = uri + "dateInterval/" + endOrStart + "/" + dateOntologyProperty.name + "/" + dateOntologyProperty.name
    val dateIntervalPom = modelWrapper.addPredicateObjectMap(uniqueString)
    modelWrapper.addResourceAsPropertyToResource(triplesMap, Prefixes("rr") + "predicateObjectMap", dateIntervalPom)

    //add dcterms:type to predicate
    modelWrapper.addPropertyAsPropertyToResource(dateIntervalPom, Prefixes("dcterms") + "type", Prefixes("dbf") + "dateIntervalMapping")

    //add predicate to start date pom
    modelWrapper.addPropertyAsPropertyToResource(dateIntervalPom, Prefixes("rr") + "predicate", Prefixes("ex") + "something")

    //add object map to start date pom
    val objectMapStartString = uniqueString + "/" + "IntervalFunctionMap"
    val objectMapStart = modelWrapper.addResourceWithPredicate(objectMapStartString, Prefixes("fnml") + "FunctionTermMap")
    modelWrapper.addResourceAsPropertyToResource(dateIntervalPom, Prefixes("rr") + "objectMap", objectMapStart)

    //add triples map to object map
    val triplesMapStartString = objectMapStartString + "/TriplesMap"
    val triplesMapStart = modelWrapper.addTriplesMap(triplesMapStartString)
    modelWrapper.addResourceAsPropertyToResource(objectMapStart, Prefixes("fnml") + "functionValue", triplesMapStart)

    //add logical source to triples map
    modelWrapper.addResourceAsPropertyToResource(triplesMapStart, Prefixes("rml") + "logicalSource", modelWrapper.logicalSource)

    //add subject map to triples map
    val subjectMapStartString = triplesMapStartString + "/SubjectMap"
    val subjectMap = modelWrapper.addResource(subjectMapStartString)
    modelWrapper.addResourceAsPropertyToResource(triplesMapStart, Prefixes("rr") + "subjectMap", subjectMap)

    //add termtype and class to subject map
    modelWrapper.addPropertyAsPropertyToResource(subjectMap, Prefixes("rr") + "termType", Prefixes("rr") + "BlankNode")
    modelWrapper.addPropertyAsPropertyToResource(subjectMap, Prefixes("rr") + "class", Prefixes("fno") + "Execution")

    //add pom blank node to triples map
    val pomBlankNode = modelWrapper.addBlankNode()
    modelWrapper.addResourceAsPropertyToResource(triplesMapStart, Prefixes("rr") + "predicateObjectMap", pomBlankNode)
    modelWrapper.addPropertyAsPropertyToResource(pomBlankNode, Prefixes("rr") + "predicate", Prefixes("fno") + "executes")

    //add object map blank node to pom blank node
    val omBlankNode = modelWrapper.addBlankNode()
    modelWrapper.addResourceAsPropertyToResource(pomBlankNode, Prefixes("rr") + "objectMap", omBlankNode)
    modelWrapper.addPropertyAsPropertyToResource(omBlankNode, Prefixes("rr") + "constant", Prefixes("dbf") + dateOntologyProperty.name)

    //add pom blank node to triples map
    val pomBlankNode2 = modelWrapper.addBlankNode()
    modelWrapper.addResourceAsPropertyToResource(triplesMapStart, Prefixes("rr") + "predicateObjectMap", pomBlankNode2)

    //add predicate to pom blank node 2
    modelWrapper.addPropertyAsPropertyToResource(pomBlankNode2, Prefixes("rr") + "predicate", Prefixes("ex") + "parameter")

    //add object map blank node to pom blank node 2
    val omBlankNode2 = modelWrapper.addBlankNode()
    modelWrapper.addResourceAsPropertyToResource(pomBlankNode2, Prefixes("rr") + "objectMap", omBlankNode2)
    modelWrapper.addLiteralAsPropertyToResource(omBlankNode2, Prefixes("rml") + "reference", mapping.templateProperty)

  }





  def addGeoCoordinatesMapping(mapping: GeoCoordinatesMapping) =
  {
    val uri = baseName("")
    addGeoCoordinatesMappingToTriplesMap(mapping, uri)
  }

  def addGeoCoordinatesMappingToTriplesMap(mapping: GeoCoordinatesMapping, uri: String) =
  {

    println("Geocoordinates Mapping not supported")

    val uniqueUri = uri + "/GeoCoordinatesMapping/"


    if(mapping.coordinates != null) {
      //first case: pair of coordinates is given
      //TODO: implement
    } else if (mapping.latitude != null && mapping.longitude != null) {
      //second case: only latitude and longitude is given
      //TODO: implement
    } else {
      //third case: 8 values are given for calculating longitude and latitude
      //TODO: implement
    }
  }





  def addConditionalMapping(mapping: ConditionalMapping) =
  {
    //TODO: implement
    println("Conditional Mappings not supported")
  }





  def addIntermediateNodeMapping(mapping: IntermediateNodeMapping) =
  {

    //create the predicate object map and it to the triples map
    val templateString = "IntermediateNodeMapping/" + mapping.nodeClass.name + "/" + mapping.correspondingProperty.name
    val uniqueString = baseName(templateString)
    val predicateObjectMap = modelWrapper.addPredicateObjectMap(uniqueString)
    modelWrapper.addPropertyAsPropertyToResource(predicateObjectMap, Prefixes("rr") + "predicate", mapping.correspondingProperty.uri)
    modelWrapper.addPredicateObjectMapUriToTriplesMap(uniqueString, modelWrapper.triplesMap)

    //add dcterms:type to predicate:
    modelWrapper.addPropertyAsPropertyToResource(predicateObjectMap, Prefixes("dcterms") + "type", Prefixes("dbf") + "intermediateNodeMapping" )

    //create the triples map with its subject map and object map
    val subjectMap = modelWrapper.addResourceWithPredicate(baseName(templateString + "/SubjectMap"), Prefixes("rr") + "SubjectMap")
    modelWrapper.addPropertyAsPropertyToResource(subjectMap, Prefixes("rr") + "constant", "tobeDefined")
    val triplesMap = modelWrapper.addTriplesMap(baseName(templateString + "/TriplesMap"), subjectMap)
    val objectMap = modelWrapper.addBlankNode()
    modelWrapper.addResourceAsPropertyToResource(objectMap, Prefixes("rr") + "parentTriplesMap", triplesMap)
    modelWrapper.addResourceAsPropertyToResource(predicateObjectMap, Prefixes("rr") + "objectMap", objectMap)

    //create the intermediate mappings
    for(mapping <- mapping.mappings) {
      addPropertyMapping(mapping, triplesMap)
    }

  }

  def addConstantMapping(mapping: ConstantMapping) =
  {
    //TODO: implement
    println("Constant Mappings not supported")
  }




  /**
    * Returns the base name + name added
    */
  private def baseName(name : String): String =
  {
    "http://mappings.dbpedia.org/wiki/" + modelWrapper.wikiTitle.encodedWithNamespace + "/" + name
  }

  private def addUnitToPredicateObjectMap(predicateObjectMap: Resource, unit : Datatype): Unit =
  {
    val objectMap = modelWrapper.addBlankNode()
    modelWrapper.addPropertyAsPropertyToResource(objectMap, Prefixes("rr") + "parentTriplesMap", unit.uri)
    modelWrapper.addResourceAsPropertyToResource(predicateObjectMap, Prefixes("rr") + "objectMap", objectMap)
  }

  /**
    * Adds mappings (this is used by intermediate node mappings
    */
  private def addPropertyMapping(mapping: PropertyMapping, triplesMap: Resource) =
  {
    mapping.getClass.getSimpleName match {
      case "SimplePropertyMapping" => addSimplePropertyMappingToTriplesMap(mapping.asInstanceOf[SimplePropertyMapping],
        triplesMap, triplesMap.getNameSpace)
      case "CalculateMapping" => println("Intermediate Calculate Mapping not supported.")
      case "CombineDateMapping" => println("Intermediate Combine Date Mapping not supported.")
      case "DateIntervalMapping" => addDateIntervalMappingToTriplesMap(mapping.asInstanceOf[DateIntervalMapping], triplesMap, triplesMap.getNameSpace)
      case "GeoCoordinatesMapping" => println("Intermediate GeoCoordinates Mapping not supported.")
      case "ConditionalMapping" => println("Intermediate Conditional Mapping not supported.")
      case "IntermediateNodeMapping" => println("Intermediate Intermediate Mapping not supported.")
      case "ConstantMapping" => println("Constant Mapping not supported.")
    }
  }
  
}
