package org.springframework.data.gremlin.schema;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.data.gremlin.schema.generator.AnnotatedSchemaGenerator;
import org.springframework.data.gremlin.schema.generator.DefaultSchemaGenerator;
import org.springframework.data.gremlin.schema.generator.SchemaGenerator;
import org.springframework.data.gremlin.schema.generator.SchemaGeneratorException;
import org.springframework.data.gremlin.schema.property.GremlinDynamicProperty;
import org.springframework.data.gremlin.schema.property.GremlinProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A BeanFactoryPostProcessor for generating {@link GremlinSchema}s and registering them with Spring.
 *
 * @author Gman
 */
public class GremlinBeanPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinBeanPostProcessor.class);

    private Set<Class<?>> embeddedClasses = new HashSet<Class<?>>();
    private Set<Class<?>> vertexClasses = new HashSet<Class<?>>();
    private Set<Class<?>> edgeClasses = new HashSet<Class<?>>();
    private Map<Class<?>, GremlinSchema<?>> schemaMap = new HashMap<Class<?>, GremlinSchema<?>>();

    private SchemaGenerator schemaGenerator;

    public GremlinBeanPostProcessor(String baseClasspath) {
        this(new DefaultSchemaGenerator(), baseClasspath);
    }

    public GremlinBeanPostProcessor(Set<Class<?>> vertexClasses, Set<Class<?>> embedded, Set<Class<?>> edgeClasses) {
        this(new DefaultSchemaGenerator(), vertexClasses, embedded, edgeClasses);
    }

    public GremlinBeanPostProcessor(SchemaGenerator schemaGenerator, String baseClasspath) {
        Assert.notNull(schemaGenerator, "The SchemaRepository is useless without a SchemaBuilder.");
        Assert.isTrue(schemaGenerator instanceof AnnotatedSchemaGenerator);
        Assert.notNull(baseClasspath, "The SchemaRepository is useless without entities. Please add a base baseClasspath.");

        AnnotatedSchemaGenerator annotatedSchemaGenerator = (AnnotatedSchemaGenerator) schemaGenerator;

        this.schemaGenerator = schemaGenerator;
        if (!StringUtils.isEmpty(baseClasspath)) {
            Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage(baseClasspath)).setScanners(new TypeAnnotationsScanner()));

            Set<Class<?>> vertexClasses = reflections.getTypesAnnotatedWith(annotatedSchemaGenerator.getVertexAnnotationType());
            this.vertexClasses.addAll(vertexClasses);
            schemaGenerator.setVertexClasses(vertexClasses);

            Class<? extends Annotation> embedType = annotatedSchemaGenerator.getEmbeddedAnnotationType();
            if (embedType != null) {
                Set<Class<?>> embeddableClasses = reflections.getTypesAnnotatedWith(embedType);
                this.embeddedClasses.addAll(embeddableClasses);
                schemaGenerator.setEmbeddedClasses(embeddableClasses);
            }

            Class<? extends Annotation> relType = annotatedSchemaGenerator.getEdgeAnnotationType();
            if (relType != null) {
                Set<Class<?>> edgeClasses = reflections.getTypesAnnotatedWith(relType);
                this.edgeClasses.addAll(edgeClasses);
                schemaGenerator.setEdgeClasses(this.edgeClasses);
            }
        }

        generateSchemasFromEntities(vertexClasses);
        generateSchemasFromEntities(edgeClasses);
        init();
    }

    public GremlinBeanPostProcessor(SchemaGenerator schemaGenerator, Set<Class<?>> vertexClasses, Set<Class<?>> embeddedClasses, Set<Class<?>> edgeClasses) {
        Assert.notNull(schemaGenerator, "The SchemaRepository is useless without a SchemaBuilder.");
        Assert.notNull(vertexClasses, "The SchemaRepository is useless without entities.");
        Assert.notEmpty(vertexClasses, "The SchemaRepository is useless without vertexClasses.");
        this.schemaGenerator = schemaGenerator;

        this.vertexClasses = vertexClasses;
        this.embeddedClasses = embeddedClasses;
        this.edgeClasses = edgeClasses;
        schemaGenerator.setVertexClasses(this.vertexClasses);
        schemaGenerator.setEmbeddedClasses(this.embeddedClasses);
        schemaGenerator.setEdgeClasses(edgeClasses);

        generateSchemasFromEntities(this.vertexClasses);
        generateSchemasFromEntities(this.edgeClasses);
        init();
    }

    /**
     * Initialises by running through each entity class, building it's {@link GremlinSchema}, caching it
     * and finally writing the {@link GremlinSchema}. It then does the same for all Embeddable classes,
     * except it doesn't write the schema
     */
    public void init() {

        // For each of the properties of each of the Schemas, assign the related Schema to properties which are of type related.
        for (Class<?> cls : schemaMap.keySet()) {
            GremlinSchema<?> schema = schemaMap.get(cls);
            schema.getProperties().stream().filter(new Predicate<GremlinProperty>() {
                @Override
                public boolean test(GremlinProperty property) {return property instanceof GremlinRelatedProperty;}
            }).forEach(new Consumer<GremlinProperty>() {
                @Override
                public void accept(GremlinProperty property) {
                    GremlinSchema<?> relatedSchema;
                    if (property instanceof GremlinDynamicProperty) {
                        GremlinDynamicProperty dynamicProperty = (GremlinDynamicProperty) property;
                        relatedSchema = schemaGenerator.generateDynamicSchema(dynamicProperty.getRelatedClassName(), dynamicProperty.getType());
                    } else {
                        relatedSchema = schemaMap.get(property.getType());
                    }
                    if (relatedSchema != null) {
                        ((GremlinRelatedProperty) property).setRelatedSchema(relatedSchema);
                    }
                }
            });

        }
    }

    private void generateSchemasFromEntities(Set<Class<?>> classes) {
        for (Class<?> cls : classes) {
            generateSchemaFromClass(cls);
        }
    }

    private <V> void generateSchemaFromClass(Class<V> cls) {

        try {
            GremlinSchema<V> schema = schemaGenerator.generateSchema(cls);
            schemaMap.put(cls, schema);
        } catch (SchemaGeneratorException e) {
            LOGGER.error(String.format("Could not generate Schema for embeddable %s. ERROR: %s", cls, e.getMessage()));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (Class<?> cls : schemaMap.keySet()) {
            GremlinSchema<?> schema = schemaMap.get(cls);
            beanFactory.registerSingleton("gremlin" + schema.getClassName() + "Schema", schema);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
