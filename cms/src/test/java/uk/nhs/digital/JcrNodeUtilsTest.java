package uk.nhs.digital;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static uk.nhs.digital.test.util.TimeTestUtils.calendarFrom;

import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.StringValue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onehippo.repository.mock.MockNode;
import org.onehippo.repository.mock.MockNodeIterator;
import uk.nhs.digital.toolbox.exception.ExceptionUtils;

import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.jcr.*;

public class JcrNodeUtilsTest {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void streamOf_returnsNodesFromNodeIteratorAsStream() {

        // given
        final MockNode nodeA = new MockNode("nodeA");
        final MockNode nodeB = new MockNode("nodeB");
        final MockNode nodeC = new MockNode("nodeC");

        final NodeIterator nodeIterator = new MockNodeIterator(asList(nodeA, nodeB, nodeC));

        // when
        final Stream<Node> actualNodeStream = JcrNodeUtils.streamOf(nodeIterator);

        // then
        assertThat(
            "Returned stream contains nodes from the iterator, exactly.",
            actualNodeStream.collect(toList()),
            containsInAnyOrder(nodeA, nodeB, nodeC)
        );
    }

    @Test
    public void getSessionQuietly_returnsNodeSession() throws RepositoryException {

        // given
        final Session expectedSession = mock(Session.class);

        final Node node = mock(Node.class);
        given(node.getSession()).willReturn(expectedSession);

        // when
        final Session actualSession = JcrNodeUtils.getSessionQuietly(node);

        // then
        assertThat(
            "Actual session returned is the one associated with the node.",
            actualSession,
            sameInstance(expectedSession)
        );
    }

    @Test
    public void getSessionQuietly_throwsException_onFailure() throws RepositoryException {

        // given
        final RepositoryException repositoryException = new RepositoryException();

        final Node node = mock(Node.class);
        given(node.getSession()).willThrow(repositoryException);

        expectedException.expect(ExceptionUtils.UncheckedWrappingException.class);
        expectedException.expectCause(sameInstance(repositoryException));

        // when
        JcrNodeUtils.getSessionQuietly(node);

        // then
        // expectations set in 'given' are satisfied
    }

    @Test
    public void getStringPropertyQuietly_returnsValueOfGivenPropertyOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final String expectedPropertyValue = "expectedPropertyValue";

        final Property expectedProperty = mock(Property.class);
        given(expectedProperty.getString()).willReturn(expectedPropertyValue);

        final Node node = mock(Node.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);
        given(node.hasProperty(expectedPropertyName)).willReturn(true);

        // when
        final Optional<String> actualPropertyValue = JcrNodeUtils.getStringPropertyQuietly(node,
            expectedPropertyName);

        // then
        assertThat(
            "Value returned was read from the given node",
            actualPropertyValue.get(),
            is(expectedPropertyValue)
        );
    }

    @Test
    public void getStringPropertyQuietly_throwsException_onFailure() throws RepositoryException {

        // given
        final RepositoryException repositoryException = new RepositoryException();

        final Node node = mock(Node.class);
        given(node.getProperty(any())).willThrow(repositoryException);
        given(node.hasProperty(any())).willReturn(true);

        expectedException.expect(ExceptionUtils.UncheckedWrappingException.class);
        expectedException.expectCause(sameInstance(repositoryException));

        // when
        JcrNodeUtils.getStringPropertyQuietly(node, "aProperty");

        // then
        // expectations set in 'given' are satisfied
    }

    @Test
    public void setStringPropertyQuietly_setsGivenValueOnGivenNodeInGivenProperty() throws RepositoryException {

        // given
        final Node node = mock(Node.class);

        final String expectedPropertyName = "expectedPropertyName";
        final String expectedPropertyValue = "expectedPropertyValue";

        // when
        JcrNodeUtils.setStringPropertyQuietly(node, expectedPropertyName, expectedPropertyValue);

        // then
        then(node).should().setProperty(expectedPropertyName, expectedPropertyValue);
    }

    @Test
    public void setStringPropertyQuietly_throwsException_onFailure() throws RepositoryException {

        // given
        final RepositoryException repositoryException = new RepositoryException();

        final Node node = mock(Node.class);
        given(node.setProperty(any(String.class), any(String.class))).willThrow(repositoryException);

        expectedException.expect(ExceptionUtils.UncheckedWrappingException.class);
        expectedException.expectCause(sameInstance(repositoryException));

        // when
        JcrNodeUtils.setStringPropertyQuietly(node, "aPropertyName", "aPropertyValue");

        // then
        // expectations set in 'given' are satisfied
    }

    @Test
    public void getMultipleStringPropertyQuietly_returnsValuesOfGivenPropertyOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final List<String> expectedPropertyValues = asList("value-a", "value-b");

        final Property expectedProperty = mock(Property.class);
        final StringValue[] storedPropertyValues = fromStrings(expectedPropertyValues);

        given(expectedProperty.getValues()).willReturn(storedPropertyValues);

        final Node node = mock(Node.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);
        given(node.hasProperty(expectedPropertyName)).willReturn(true);

        // when
        final List<String> actualPropertyValues =
            JcrNodeUtils.getMultipleStringPropertyQuietly(node, expectedPropertyName);

        // then
        assertThat(
            "Value returned was read from the given node",
            actualPropertyValues,
            is(expectedPropertyValues)
        );
    }

    @Test
    public void setMultipleStringPropertyQuietly_savesGivenListOfValuesOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final String[] expectedPropertyValues = new String[]{"value-a", "value-b"};
        final List<String> incomingPropertyValues = asList(expectedPropertyValues);

        final Node node = mock(Node.class);
        final Property expectedProperty = mock(Property.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);

        // when
        JcrNodeUtils.setMultipleStringPropertyQuietly(node, expectedPropertyName, incomingPropertyValues);

        // then
        then(expectedProperty).should().setValue(expectedPropertyValues);
    }

    @Test
    public void setMultipleStringPropertyQuietly_savesGivenArrayOfValuesOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final String[] expectedPropertyValues = new String[]{"value-a", "value-b"};

        final Node node = mock(Node.class);
        final Property expectedProperty = mock(Property.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);

        // when
        JcrNodeUtils.setMultipleStringPropertyQuietly(node, expectedPropertyName, expectedPropertyValues);

        // then
        then(expectedProperty).should().setValue(expectedPropertyValues);
    }

    @Test
    public void getInstantPropertyQuietly_returnsValueOfGivenPropertyOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final Instant expectedPropertyValue = Instant.parse("2020-05-04T10:30:00.000Z");
        final Calendar expectedCalendar = calendarFrom(expectedPropertyValue);

        final Property expectedProperty = mock(Property.class);
        given(expectedProperty.getDate()).willReturn(expectedCalendar);

        final Node node = mock(Node.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);
        given(node.hasProperty(expectedPropertyName)).willReturn(true);

        // when
        final Optional<Instant> actualValue =
            JcrNodeUtils.getInstantPropertyQuietly(node, expectedPropertyName);

        // then
        assertThat(
            "Value returned was read from the given node",
            actualValue,
            is(Optional.of(expectedPropertyValue))
        );
    }

    @Test
    public void getInstantPropertyQuietly_throwsException_onFailure() throws RepositoryException {

        // given
        final RepositoryException repositoryException = new RepositoryException();

        final Node node = mock(Node.class);
        given(node.getProperty(any(String.class))).willThrow(repositoryException);
        given(node.hasProperty(any(String.class))).willReturn(true);

        expectedException.expect(ExceptionUtils.UncheckedWrappingException.class);
        expectedException.expectCause(sameInstance(repositoryException));

        // when
        JcrNodeUtils.getInstantPropertyQuietly(node, "aPropertyName");

        // then
        // expectations set in 'given' are satisfied
    }

    @Test
    public void setInstantPropertyQuietly() throws RepositoryException {

        // given
        final Node node = mock(Node.class);

        final String expectedPropertyName = "expectedPropertyName";
        final Instant expectedPropertyValue = Instant.parse("2020-05-04T10:30:00.000Z");

        // when
        JcrNodeUtils.setInstantPropertyQuietly(node, expectedPropertyName, expectedPropertyValue);

        // then
        then(node).should().setProperty(expectedPropertyName, calendarFrom(expectedPropertyValue));
    }

    @Test
    public void getMultipleInstantPropertyQuietly_returnsValuesOfGivenPropertyOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final List<Instant> expectedPropertyValues = asList(
            Instant.parse("2020-05-04T10:30:00.000Z"),
            Instant.parse("2020-05-04T10:30:00.001Z")
        );

        final Property expectedProperty = mock(Property.class);
        final DateValue[] storedPropertyValues = fromInstants(expectedPropertyValues);

        given(expectedProperty.getValues()).willReturn(storedPropertyValues);

        final Node node = mock(Node.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);
        given(node.hasProperty(expectedPropertyName)).willReturn(true);

        // when
        final List<Instant> actualPropertyValues =
            JcrNodeUtils.getMultipleInstantPropertyQuietly(node, expectedPropertyName);

        // then
        assertThat(
            "Value returned was read from the given node",
            actualPropertyValues,
            is(expectedPropertyValues)
        );
    }

    @Test
    public void setMultipleInstantPropertyQuietly_savesGivenListOfValuesOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";
        final List<Instant> incomingPropertyValues = asList(
            Instant.parse("2020-05-04T10:30:00.000Z"),
            Instant.parse("2020-05-04T10:30:00.001Z")
        );
        final DateValue[] expectedPropertyValues = fromInstants(incomingPropertyValues);

        final Node node = mock(Node.class);
        final Property expectedProperty = mock(Property.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);

        // when
        JcrNodeUtils.setMultipleInstantPropertyQuietly(node, expectedPropertyName, incomingPropertyValues);

        // then
        then(expectedProperty).should().setValue(expectedPropertyValues);
    }

    @Test
    public void setMultipleInstantPropertyQuietly_savesGivenArrayOfValuesOnGivenNode() throws RepositoryException {

        // given
        final String expectedPropertyName = "expectedPropertyName";

        final Instant[] incomingPropertyValues = new Instant[]{
            Instant.parse("2020-05-04T10:30:00.000Z"),
            Instant.parse("2020-05-04T10:30:00.001Z")
        };

        final DateValue[] expectedPropertyValues = fromInstants(incomingPropertyValues);

        final Node node = mock(Node.class);
        final Property expectedProperty = mock(Property.class);
        given(node.getProperty(expectedPropertyName)).willReturn(expectedProperty);

        // when
        JcrNodeUtils.setMultipleInstantPropertyQuietly(node, expectedPropertyName, incomingPropertyValues);

        // then
        then(expectedProperty).should().setValue(expectedPropertyValues);
    }

    @Test
    public void validateIsOfTypeHandle_deemsNodeValid_whenItsJcrPrimaryTypeIsHippoHandle() throws RepositoryException {

        // given
        final Node node = mock(Node.class);
        given(node.isNodeType("hippo:handle")).willReturn(true);

        // when
        JcrNodeUtils.validateIsOfTypeHandle(node);

        // then
        // no exception is thrown
    }

    @Test
    public void validateIsOfTypeHandle_throwsException_whenItsJcrPrimaryTypeIsNotHippoHandle() throws RepositoryException {

        // given
        final Node node = mock(Node.class);
        given(node.isNodeType("hippo:handle")).willReturn(false);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Node's 'jcr:primaryType' property has to be of type 'hippo:handle'");

        // when
        JcrNodeUtils.validateIsOfTypeHandle(node);

        // then
        // expectations set in 'given' are satisfied
    }

    private static DateValue[] fromInstants(final Instant[] values) {
        return fromInstants(asList(values));
    }

    private static DateValue[] fromInstants(final List<Instant> values) {
        return values.stream()
            .map(instant -> new Calendar.Builder().setInstant(Date.from(instant)).build())
            .map(DateValue::new)
            .toArray(DateValue[]::new);
    }

    private static StringValue[] fromStrings(final List<String> values) {
        return values.stream().map(StringValue::new).toArray(StringValue[]::new);
    }
}
