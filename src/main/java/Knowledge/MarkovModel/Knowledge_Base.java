package Knowledge.MarkovModel;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.reasoner.Derivation;

import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.modify.request.UpdateAdd;
import org.apache.jena.system.Txn;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;


import java.io.*;
import java.util.*;

public class Knowledge_Base {
    private static String successor_ns = "http://www.semanticweb.org/mohamed/ontologies/2020/2/DedaleOnt#successor";
    private static String positionated_ns = "http://www.semanticweb.org/mohamed/ontologies/2020/2/DedaleOnt#isPositionnated";
    private static String node_ns = "http://www.semanticweb.org/mohamed/ontologies/2020/2/DedaleOnt#Node";
    private static String owl_ns = "http://www.semanticweb.org/mohamed/ontologies/2020/2/DedaleOnt#";

    public static void printStatements(Model m, Resource s, Property p, Resource o) {
        for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) { Statement stmt = i.nextStatement(); System.out.println(" - " + PrintUtil.print(stmt)); }
    }


    //public static void hermitExample(){
    //    // First, we create an OWLOntologyManager object. The manager will load and
    //    // save ontologies.
    //    OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
    //    // Now, we create the file from which the ontology will be loaded.
    //    // Here the ontology is stored in a file locally in the ontologies subfolder
    //    // of the examples folder.
    //    File inputOntologyFile = new File("src/main/java/Knowledge/MarkovModel/dedaleOntology.owl");
    //    // We use the OWL API to load the ontology.
    //    OWLOntology ontology=manager.loadOntologyFromOntologyDocument(inputOntologyFile);
//
    //    // Now we can start and create the reasoner. Since materialisation of axioms is controlled
    //    // by OWL API classes and is not natively supported by HermiT, we need to instantiate HermiT
    //    // as an OWLReasoner. This is done via a ReasonerFactory object.
    //    Reasoner.ReasonerFactory factory = new Reasoner.ReasonerFactory();
//
    //    // The factory can now be used to obtain an instance of HermiT as an OWLReasoner.
    //    Configuration c=new Configuration();
    //    c.reasonerProgressMonitor=new ConsoleProgressMonitor();
    //    OWLReasoner reasoner=factory.createReasoner(ontology, c);
    //    // The following call causes HermiT to compute the class, object,
    //    // and data property hierarchies as well as the class instances.
    //    // Hermit does not yet support precomputation of property instances.
    //    //reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY, InferenceType.OBJECT_PROPERTY_ASSERTIONS);
    //    // We now have to decide which kinds of inferences we want to compute. For different types
    //    // there are different InferredAxiomGenerator implementations available in the OWL API and
    //    // we use the InferredSubClassAxiomGenerator and the InferredClassAssertionAxiomGenerator
    //    // here. The different generators are added to a list that is then passed to an
    //    // InferredOntologyGenerator.
    //    List<InferredAxiomGenerator<? extends OWLAxiom>> generators=new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
    //    generators.add(new InferredSubClassAxiomGenerator());
    //    generators.add(new InferredClassAssertionAxiomGenerator());
    //    // We dynamically overwrite the default disjoint classes generator since it tries to
    //    // encode the reasoning problem itself instead of using the appropriate methods in the
    //    // reasoner. That bypasses all our optimisations and means there is not progress report :-(
    //    // We don't want that!
    //    generators.add(new InferredDisjointClassesAxiomGenerator() {
    //        boolean precomputed=false;
    //        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLDisjointClassesAxiom> result) {
    //            if (!precomputed) {
    //                reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
    //                precomputed=true;
    //            }
    //            for (OWLClass cls : reasoner.getDisjointClasses(entity).getFlattened()) {
    //                result.add(dataFactory.getOWLDisjointClassesAxiom(entity, cls));
    //            }
    //        }
    //    });
    //    // We can now create an instance of InferredOntologyGenerator.
    //    InferredOntologyGenerator iog=new InferredOntologyGenerator(reasoner,generators);
    //    // Before we actually generate the axioms into an ontology, we first have to create that ontology.
    //    // The manager creates the for now empty ontology for the inferred axioms for us.
    //    OWLOntology inferredAxiomsOntology=manager.createOntology();
    //    // Now we use the inferred ontology generator to fill the ontology. That might take some
    //    // time since it involves possibly a lot of calls to the reasoner.
    //    iog.fillOntology(manager, inferredAxiomsOntology);
    //    // Now the axioms are computed and added to the ontology, but we still have to save
    //    // the ontology into a file. Since we cannot write to relative files, we have to resolve the
    //    // relative path to an absolute one in an OS independent form. We do this by (virtually) creating a
    //    // file with a relative path from which we get the absolute file.
    //    File inferredOntologyFile=new File("src/main/java/Knowledge/MarkovModel/x.owl");
    //    if (!inferredOntologyFile.exists())
    //        inferredOntologyFile.createNewFile();
    //    inferredOntologyFile=inferredOntologyFile.getAbsoluteFile();
    //    // Now we create a stream since the ontology manager can then write to that stream.
    //    OutputStream outputStream=new FileOutputStream(inferredOntologyFile);
    //    // We use the same format as for the input ontology.
    //    manager.saveOntology(inferredAxiomsOntology, manager.getOntologyFormat(ontology), outputStream);
    //    // Now that ontology that contains the inferred axioms should be in the ontologies subfolder
    //    // (you Java IDE, e.g., Eclipse, might have to refresh its view of files in the file system)
    //    // before the file is visible.
    //    System.out.println("The ontology in examples/ontologies/pizza-inferred.owl should now contain all inferred axioms (you might need to refresh the IDE file view). ");
//
    //    //OWLOntologyManager m= OWLManager.createOWLOntologyManager();
    //    //OWLOntology o= null;
    //    //try {
    //    //    o = m.loadOntologyFromOntologyDocument(IRI.create("file:src/main/java/Knowledge/MarkovModel/dedaleOntology.owl"));
    //    //} catch (OWLOntologyCreationException e) {
    //    //    e.printStackTrace();
    //    //}
    //    //Reasoner hermit=new Reasoner(o);
    //    //hermit.precomputeSameAsEquivalenceClasses();
    //    //hermit.precomputeInferences(InferenceType.OBJECT_PROPERTY_ASSERTIONS,InferenceType.OBJECT_PROPERTY_HIERARCHY);
    //}


    public static void main(String[] args) throws IOException {
        Model model = ModelFactory.createDefaultModel();
        model.read( "src/main/java/Knowledge/MarkovModel/datas.ttl" );
        Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL("src/main/java/Knowledge/MarkovModel/rules.txt"));

        InfModel infModel = ModelFactory.createInfModel( reasoner, model );
        Model m2 = infModel.getDeductionsModel();
        Dataset ds = DatasetFactory.create(infModel);





        RDFConnection conn = RDFConnectionFactory.connect(ds);
        //conn.load("src/main/java/Knowledge/MarkovModel/datas.ttl");
        QueryExecution qExec = conn.query("SELECT ?s ?p ?o { ?s ?p ?o }") ;
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource subject = qs.getResource("s") ;
            RDFNode p = qs.get("p");
            Resource s = qs.getResource("o");
            System.out.println("Subject: "+subject+",  "+p+" : " + s) ;
        }
        qExec.close() ;
        conn.close() ;

        //StmtIterator it = infModel.listStatements();
        //while ( it.hasNext() )
        //{
        //    Statement stmt = it.nextStatement();
        //    Resource subject = stmt.getSubject();
        //    Property predicate = stmt.getPredicate();
        //    RDFNode object = stmt.getObject();
        //    System.out.println( subject.toString() + " " + predicate.toString() + " " + object.toString() );
        //}

    }






    }

