package uk.nhs.digital.common.components.apispecification.handlebars.schema;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static uk.nhs.digital.test.util.ReflectionTestUtils.setField;
import static uk.nhs.digital.test.util.StringTestUtils.Placeholders.placeholders;
import static uk.nhs.digital.test.util.StringTestUtils.ignoringUuids;
import static uk.nhs.digital.test.util.StringTestUtils.ignoringWhiteSpacesIn;
import static uk.nhs.digital.test.util.TestFileUtils.contentOfFileFromClasspath;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Options;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import uk.nhs.digital.common.components.apispecification.handlebars.MarkdownHelper;
import uk.nhs.digital.test.util.StringTestUtils.Placeholders;
import uk.nhs.digital.test.util.TestDataCache;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Function;

@RunWith(DataProviderRunner.class)
public class SchemaHelperTest {

    // Note the use of StringTestUtils.ignoringWhiteSpacesIn in assertions.
    // Removal of blank lines improves readability when debugging tests,
    // and reduces the number of times test data files need updating.
    //
    // In test cases that focus on, say, a small number of specific fields,
    // the actual output often contains many blank lines which makes it harder
    // to see the complete structure in one glance. Blank lines are redundant in
    // HTML output and removing them shrinks the content to a size that makes
    // is much easier to analyse it.
    //
    // Changes to the templates result in the updated output to stop matching
    // the test files, but the differences are very often limited to the blank
    // lines which, as said above, have no actual impact on the displayed
    // content. Ignoring those lines during assertions reduces the number of
    // times the test files need to be updated whilst not invalidating their
    // value as a reference content.

    @Rule public ExpectedException expectedException = ExpectedException.none();

    private static final TestDataCache cache = TestDataCache.create();

    private static final String PROPERTY_PLACEHOLDER_X_OF = "xOfPropertyPlaceholder";

    private SchemaHelper schemaHelper;

    private MarkdownHelper markdownHelper;

    @Before
    public void setUp() {
        markdownHelper = mock(MarkdownHelper.class);

        given(markdownHelper.apply(eq("Test Schema Object description in `Markdown`."), any(Options.class)))
            .willReturn("Test Schema Object description in <code>Markdown rendered to HTML</code>.");

        final String templatesDir = Paths.get("../webapp/src/main/resources/api-specification/codegen-templates").toAbsolutePath().normalize().toString();

        schemaHelper = new SchemaHelper(markdownHelper, templatesDir);
    }

    @Test
    public void rendersAllSimpleFieldsOfSingleSchemaObjectAsHtml() {

        // given
        final String expectedSchemaHtml = readFrom("schemaObjectCompleteTopLevel-simpleFields.html");

        final Schema<?> schemaObject = fromJsonFile("schemaObjectCompleteTopLevel-simpleFields.json");

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("All 'simple' fields of Schema Object are rendered in HTML.",
            ignoringUuids(ignoringWhiteSpacesIn(actualSchemaHtml)),
            is(ignoringUuids(ignoringWhiteSpacesIn(expectedSchemaHtml)))
        );
        // Note that 'Example' field is not rendered when enum (Allowed Values) is defined.
    }

    @Test
    @UseDataProvider("falsyValues")
    public void rendersFalsyFieldsSetToZero(
        final String propertyName,
        final Object propertyValue,
        final Function<Schema<?>, Schema<?>> propertySetter
    ) {
        // given
        final Schema<?> schemaObject = propertySetter.apply(new ObjectSchema());

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Property '" + propertyName + "' is rendered for falsy value '" + propertyValue + "'.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString(propertyName + ": <span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">" + propertyValue + "</span>")
        );
    }

    @Test
    public void rendersCompleteHierarchyOfSchemaObjectsWithTheirFieldsAndIndentationsAsHtml_traversingFieldsProperties() {

        // given
        final String expectedSchemaHtml = readFrom("schemaObjectsMultiLevelHierarchy-properties.html");

        final Schema<?> schemaObject = fromJsonFile("schemaObjectsMultiLevelHierarchy-properties.json");

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("All Schema Objects in the hierarchy are rendered in HTML.",
            ignoringUuids(ignoringWhiteSpacesIn(actualSchemaHtml)),
            is(ignoringUuids(ignoringWhiteSpacesIn(expectedSchemaHtml)))
        );
    }

    @DataProvider
    public static Object[][] falsyValues() {
        // @formatter:off
        return new Object[][]{
            // propertyName     propertyValue   propertySetter
            {"Multiple of",     0,              (Function<Schema<?>, Schema<?>>) schema -> schema.multipleOf(ZERO)},
            {"Maximum",         0,              (Function<Schema<?>, Schema<?>>) schema -> schema.maximum(ZERO)},
            {"Minimum",         0,              (Function<Schema<?>, Schema<?>>) schema -> schema.minimum(ZERO)},
            {"Max length",      0,              (Function<Schema<?>, Schema<?>>) schema -> schema.maxLength(0)},
            {"Min length",      0,              (Function<Schema<?>, Schema<?>>) schema -> schema.minLength(0)},
            {"Max items",       0,              (Function<Schema<?>, Schema<?>>) schema -> schema.maxItems(0)},
            {"Min items",       0,              (Function<Schema<?>, Schema<?>>) schema -> schema.minItems(0)},
            {"Max properties",  0,              (Function<Schema<?>, Schema<?>>) schema -> schema.maxProperties(0)},
            {"Min properties",  0,              (Function<Schema<?>, Schema<?>>) schema -> schema.minProperties(0)},
            {"Example",         0,              (Function<Schema<?>, Schema<?>>) schema -> schema.example(0)},
            {"Example",         false,          (Function<Schema<?>, Schema<?>>) schema -> schema.example(false)},
        };
        // @formatter:on
    }

    @Test
    public void doesNotRenderFieldsAbsentFromSpecification() {

        // given
        final Schema<?> schemaObject = new ObjectSchema();

        final String expectedSchemaHtml = readFrom("schemaObject-empty.html");

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Fields absent from the specifications are not rendered.",
            ignoringUuids(ignoringWhiteSpacesIn(actualSchemaHtml)),
            is(ignoringUuids(ignoringWhiteSpacesIn(expectedSchemaHtml)))
        );
    }

    @Test
    @UseDataProvider("propertyNameProvider_xOf")
    public void rendersCompleteHierarchyOfSchemaObjectsWithTheirFieldsAndIndentationsAsHtml_traversingFieldsXOf(final String propertyName) {

        // given
        final String expectedSchemaHtml = readFrom("schemaObjectsMultiLevelHierarchy-xOf.html")
            .replaceAll(PROPERTY_PLACEHOLDER_X_OF, propertyName);

        final Schema<?> schemaObject = fromJsonFile("schemaObjectsMultiLevelHierarchy-xOf.json",
            placeholders().with(PROPERTY_PLACEHOLDER_X_OF, propertyName)
        );

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("All Schema Objects in the hierarchy are rendered in HTML.",
            ignoringUuids(ignoringWhiteSpacesIn(actualSchemaHtml)),
            is(ignoringUuids(ignoringWhiteSpacesIn(expectedSchemaHtml)))
        );
    }

    @Test
    public void rendersCompleteHierarchyOfSchemaObjectsWithTheirFieldsAndIndentationsAsHtml_traversingFieldsItems() {

        // given
        final String expectedSchemaHtml = readFrom("schemaObjectsMultiLevelHierarchy-items.html");

        final Schema<?> schemaObject = fromJsonFile("schemaObjectsMultiLevelHierarchy-items.json");

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("All Schema Objects in the hierarchy are rendered in HTML.",
            ignoringUuids(ignoringWhiteSpacesIn(actualSchemaHtml)),
            is(ignoringUuids(ignoringWhiteSpacesIn(expectedSchemaHtml)))
        );
    }

    @Test
    public void exclusiveMaximum_rendered_asExclusive_whenExclusiveMaximumIsTrue() {

        // given
        final Schema<?> schemaObject = new ObjectSchema()
            .maximum(ZERO)
            .exclusiveMaximum(true);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Exclusive maximum is rendered as exclusive.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString("<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">(exclusive)</span>")
        );
    }

    @Test
    public void exclusiveMaximum_rendered_asInclusive_whenExclusiveMaximumIsFalse() {

        // given
        final Schema<?> schemaObject = new ObjectSchema()
            .maximum(ZERO)
            .exclusiveMaximum(false);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Exclusive maximum is rendered as inclusive.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString("<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">(inclusive)</span>")
        );
    }

    @Test
    public void exclusiveMaximum_notRendered_whenMaximumIsAbsent() {

        // given
        final Schema<?> schemaObject = new ObjectSchema().exclusiveMaximum(true);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Exclusive maximum is not rendered.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            allOf(
                not(containsString("(inclusive)")),
                not(containsString("(exclusive)"))
            ));
    }

    @Test
    public void exclusiveMinimum_rendered_asExclusive_whenExclusiveMinimumIsTrue() {

        // given
        final Schema<?> schemaObject = new ObjectSchema()
            .minimum(ZERO)
            .exclusiveMinimum(true);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Exclusive minimum is rendered as exclusive.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString("<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">(exclusive)</span>")
        );
    }

    @Test
    public void exclusiveMinimum_rendered_asInclusive_whenExclusiveMinimumIsFalse() {

        // given
        final Schema<?> schemaObject = new ObjectSchema()
            .minimum(ZERO)
            .exclusiveMinimum(false);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Exclusive minimum is rendered as inclusive.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString("<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">(inclusive)</span>")
        );
    }

    @Test
    public void exclusiveMinimum_notRendered_whenMinimumIsAbsent() {

        // given
        final Schema<?> schemaObject = new ObjectSchema().exclusiveMinimum(true);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Exclusive minimum is not rendered.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            allOf(
                not(containsString("(inclusive)")),
                not(containsString("(exclusive)"))
            ));
    }

    @Test
    public void itemsRow_rendered_whenNeitherOfXOfPropertiesArePresent() {

        // given
        final Schema<?> schemaObject = schemaWithItemsWithNoXOf();

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("HTML contains 'items' element.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString(">items object<")
        );
    }

    @Test
    @UseDataProvider("propertyNameProvider_xOf")
    public void itemsRow_notRendered_whenEitherOfXOfPropertiesArePresent(final String propertyName) {

        // given
        final Schema<?> schemaObject = schemaWithXOfPropertyUnderItemsObject(propertyName);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("HTML does not contain 'items' row.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            not(containsString(">items object<"))
        );
    }

    @Test
    @UseDataProvider("propertyNameProvider_xOf")
    public void property_xOf_rendered_whenPresent_inObjectOtherThanItems(final String propertyName) {

        // given
        final Schema<?> schemaObject = schemaWithXOfPropertyUnderNonItemsObject(propertyName);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("HTML contains '" + propertyName + "' element.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            stringContainsInOrder(
                "padding-left: 0.0em;",           // root
                "padding-left: 1.5em;",           //   non-items
                "padding-left: 3.0em;",           //     oneOf
                ">" + propertyName + "<",       //
                "padding-left: 4.5em;",           //       A
                ">" + propertyName + " - A<",   //
                "padding-left: 4.5em;",           //       B
                ">" + propertyName + " - B<"
            )
        );
    }

    @Test
    @UseDataProvider("propertyNameProvider_xOf")
    public void property_xOf_rendered_whenPresent_inItems(final String propertyName) {

        // given
        final Schema<?> schemaObject = schemaWithXOfPropertyUnderItemsObject(propertyName);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("HTML contains '" + propertyName + "' element.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            stringContainsInOrder(
                "padding-left: 0.0em;",           // root
                "padding-left: 1.5em;",           //   array-schema
                "padding-left: 3.0em;",           //     oneOf
                ">" + propertyName + "<",       //
                "padding-left: 4.5em;",           //       A
                ">" + propertyName + " - A<",   //
                "padding-left: 4.5em;",           //       B
                ">" + propertyName + " - B<"
            )
        );
    }

    @Test
    @UseDataProvider("propertyNameProvider_xOf")
    public void property_xOf_notRendered_whenAbsent(final String propertyName) {

        // given
        final Schema<?> schemaObject = schemaWithItemsWithNoXOf();

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("HTML contains '" + propertyName + "' element.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            not(containsString(">" + propertyName + "<"))
        );
    }

    @Test
    @UseDataProvider("valuesOfVariousAnyTypes")
    public void rendersDefault_forSchemas_ofVariousTypes(
        final String testCaseDescription,
        final String schemaType,
        final String format,
        final Object defaultValueJson,
        final String expectedRenderedValue
    ) {
        // given
        final Schema<?> schemaObject = fromJsonFile("schemaObject-fieldWithPlaceholder.json",
            placeholders()
                .with("typePlaceholder", schemaType)
                .with("propertyNamePlaceholder", "default")
                .with("formatPlaceholder", format)
                .with("valuePlaceholder", defaultValueJson)
        );

        final String expectedRendering = "<div>Default: " + expectedRenderedValue + "</div>";

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Default " + testCaseDescription + " value is rendered for schema of type '" + schemaType + "'.",
            actualSchemaHtml,
            containsString(expectedRendering)
        );
    }

    @Test
    @UseDataProvider("enumValuesOfVariousTypes")
    public void rendersEnum_ofVariousTypes(
        final String testCaseDescription,
        final Object enumJson,
        final String firstRenderedValue,
        final String secondRenderedValue
    ) {
        // given
        final Schema<?> schemaObject = fromJsonFile("schemaObject-fieldWithPlaceholder.json",
            placeholders()
                .with("typePlaceholder", "object")
                .with("propertyNamePlaceholder", "enum")
                .with("valuePlaceholder", enumJson)
        );

        final String expectedRendering = format(
            "<div>Allowed values:"
                + " <span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">%s</span>,"
                + " <span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">%s</span>"
                + "</div>",
            firstRenderedValue,
            secondRenderedValue
        );

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Enum with " + testCaseDescription + " is rendered.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            containsString(expectedRendering)
        );
    }

    @DataProvider
    public static Object[][] enumValuesOfVariousTypes() {
        // @formatter:off
        return new Object[][]{
            // testCaseDescription              enumJson                        firstRenderedValue  secondRenderedValue
            {"strings",                         "[\"string-a\", \"string-b\"]", "string-a",         "string-b"},
            {"booleans",                        "[true, false]",                "true",             "false"},
            {"numbers",                         "[-1.42, 0]",                   "-1.42",            "0"},
            {"nulls",                           "[\"string-a\", null]",         "string-a",         "null"},
            {"empty strings",                   "[\"string-a\", \"\"]",         "string-a",         ""},
            {"strings with HTML-hostile chars", "[\"< >\", \"&\"]",             "&lt; &gt;",        "&amp;"},
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("valuesOfVariousAnyTypes")
    public void rendersExamples_ofVariousTypes(
        final String testCaseDescription,
        final String schemaType,
        final String format,
        final Object valueJson,
        final String renderedValue
    ) {
        // given
        final Schema<?> schemaObject = fromJsonFile("schemaObject-fieldWithPlaceholder.json",
            placeholders()
                .with("typePlaceholder", schemaType)
                .with("formatPlaceholder", format)
                .with("propertyNamePlaceholder", "example")
                .with("valuePlaceholder", valueJson)
        );

        final String expectedRendering = format("<div>Example: %s</div>", renderedValue);

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("Example with " + testCaseDescription + " is rendered.",
            actualSchemaHtml,
            containsString(expectedRendering)
        );
    }

    @DataProvider
    public static Object[][] valuesOfVariousAnyTypes() {
        // @formatter:off
        return new Object[][]{
            // testCaseDescription              schemaType  format          valueJson
            //  renderedValue

            // strings
            {"string",                          "string",   null,           "\"[string-a]&<string-b>\"",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">[string-a]&amp;&lt;string-b&gt;</span>"},

            // integers
            {"32bit integer",                   "integer",  "int32",        "-11",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">-11</span>"},

            {"64bit integer",                   "integer",  "int64",        "-12",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">-12</span>"},

            // floats
            {"float number",                    "number",   "float",        "-1.42",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">-1.42</span>"},

            {"double number",                   "number",   "double",       "-1.43",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">-1.43</span>"},

            // booleans
            {"boolean",                         "boolean",  null,           "true",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">true</span>"},

            // dates
            {"date string",                     "string",   "date",         "\"2020-02-29\"",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">2020-02-29</span>"},

            {"date-time string",                "string",   "date-time",    "\"2020-02-29T23:59:59Z\"",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">2020-02-29T23:59:59Z</span>"},

            // arrays
            {"array",                           "array",    null,           "[-1.42, 0, \"string-value\"]",
                "<span class=\"nhsd-a-text-highlight nhsd-a-text-highlight--code\">[-1.42,0,&quot;string-value&quot;]</span>"},

            {"array of JSON objects",           "array",    null,           "[{\n  \"a\": \"aa\"\n}, {\n  \"b\": \"bb\"\n}]",
                "<pre><code>[ {\n  &quot;a&quot; : &quot;aa&quot;\n}, {\n  &quot;b&quot; : &quot;bb&quot;\n} ]</code></pre>"},

            // JSON objects
            {"JSON",                            "object",   null,           "{\n  \"simple\": \"json\" \n}",
                "<pre><code>{\n  &quot;simple&quot; : &quot;json&quot;\n}</code></pre>"},

            {"JSON with HTML hostile chars",    "object",   null,           "{\n  \"hostile-chars\": \"< > &\" \n}",
                "<pre><code>{\n  &quot;hostile-chars&quot; : &quot;&lt; &gt; &amp;&quot;\n}</code></pre>"}
        };
        // @formatter:on
    }

    @Test
    public void rendersRequired_forObjectsUnderProperties_whereParentHasRequiredFieldWithTheirNames() {

        // given
        final Schema<?> schemaObject = fromJsonFile("schemaObject-requiredField.json");

        // when
        final String actualSchemaHtml = schemaHelper.apply(schemaObject, null);

        // then
        assertThat("HTML with 'required' status rendered for the appropriate object.",
            ignoringWhiteSpacesIn(actualSchemaHtml),
            stringContainsInOrder(
                ">first-object<", ">required<",
                ">second-object<",
                ">third-object<", ">required<"
            )
        );
    }

    @Test
    public void throwsExceptionOnSchemaRenderingFailure() {

        // given
        final Schema<?> schemaObject = new ObjectSchema().description("a description");

        given(markdownHelper.apply(any(), any())).willThrow(new RuntimeException());

        expectedException.expectMessage("Failed to render schema.");
        expectedException.expect(SchemaRenderingException.class);
        expectedException.expectCause(instanceOf(HandlebarsException.class));

        // when
        schemaHelper.apply(schemaObject, null);

        // then

        // expectations set up in 'given' are satisfied
    }

    @DataProvider
    public static Object[][] propertyNameProvider_xOf() {
        return new Object[][]{{"oneOf"}, {"anyOf"}, {"allOf"}};
    }

    private Schema<?> schemaWithItemsWithNoXOf() {

        final ComposedSchema items = new ComposedSchema();
        items.title("items object");

        return new ObjectSchema()
            .properties(
                ImmutableMap.of("array-schema", new ArraySchema().items(items))
            );
    }

    private Schema<?> schemaWithXOfPropertyUnderNonItemsObject(final String propertyName) {
        final ComposedSchema notItemSchemaObject = new ComposedSchema();
        notItemSchemaObject.title("not-items schema object");

        setField(notItemSchemaObject, propertyName, new ArrayList<>(asList(
            new ObjectSchema().title(propertyName + " - A"),
            new ObjectSchema().title(propertyName + " - B")
        )));

        return new ObjectSchema()
            .title("root schema object")
            .properties(
                ImmutableMap.of("not-items-schema-object", notItemSchemaObject)
            );
    }

    private Schema<?> schemaWithXOfPropertyUnderItemsObject(final String propertyName) {

        final ComposedSchema items = new ComposedSchema();
        items.title("items object");

        setField(items, propertyName, new ArrayList<>(asList(
            new ObjectSchema().title(propertyName + " - A"),
            new ObjectSchema().title(propertyName + " - B")
        )));

        return new ObjectSchema()
            .title("root object")
            .properties(
                ImmutableMap.of("array-schema", new ArraySchema().items(items))
            );
    }

    private String classPathOf(final String testDataFileName) {
        return "/test-data/api-specifications/SchemaHelperTest/" + testDataFileName;
    }

    private Schema<?> fromJsonFile(final String specJsonFileName) {
        return fromJsonFile(specJsonFileName, placeholders());
    }

    private Schema<?> fromJsonFile(
        final String specJsonTemplateFileName,
        final Placeholders placeholders
    ) {
        final String specJsonWithPlaceholders = cache.get(specJsonTemplateFileName,
            () -> contentOfFileFromClasspath(classPathOf(specJsonTemplateFileName)));

        final String specJsonWithResolvedPlaceholders = placeholders.resolveIn(specJsonWithPlaceholders);

        return fromJson(specJsonWithResolvedPlaceholders);
    }

    private Schema<?> fromJson(final String oasJson) {

        final ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);

        final OpenAPI openApi = new OpenAPIV3Parser().readContents(oasJson, null, parseOptions).getOpenAPI();

        return openApi
            .getPaths()
            .get("/test")
            .getPost()
            .getRequestBody()
            .getContent()
            .get("application/json")
            .getSchema();
    }

    private String readFrom(final String testDataFileName) {
        return cache.get(testDataFileName, () -> contentOfFileFromClasspath(classPathOf(testDataFileName)));
    }

}
