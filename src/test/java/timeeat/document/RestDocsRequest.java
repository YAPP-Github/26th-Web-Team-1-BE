package timeeat.document;

import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.util.LinkedList;
import java.util.List;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;

public class RestDocsRequest {

    private final ResourceSnippetParametersBuilder resourceBuilder;
    private final List<Snippet> snippets;

    public RestDocsRequest() {
        this.resourceBuilder = new ResourceSnippetParametersBuilder();
        this.snippets = new LinkedList<>();
    }

    public RestDocsRequest tag(Tag tag) {
        resourceBuilder.tag(tag.getDisplayName());
        return this;
    }

    public RestDocsRequest summary(String summary) {
        resourceBuilder.summary(summary);
        return this;
    }

    public RestDocsRequest description(String description) {
        resourceBuilder.description(description);
        return this;
    }

    public RestDocsRequest pathParameter(ParameterDescriptor... descriptors) {
        snippets.add(pathParameters(descriptors));
        return this;
    }

    public RestDocsRequest queryParameter(ParameterDescriptor... descriptors) {
        snippets.add(queryParameters(descriptors));
        return this;
    }

    public RestDocsRequest requestHeader(HeaderDescriptor... descriptors) {
        snippets.add(requestHeaders(descriptors));
        return this;
    }

    public RestDocsRequest requestCookie(CookieDescriptor... descriptors) {
        snippets.add(requestCookies(descriptors));
        return this;
    }

    public RestDocsRequest requestBodyField(FieldDescriptor... descriptors) {
        snippets.add(requestFields(descriptors));
        return this;
    }

    public ResourceSnippetParametersBuilder getResourceBuilder() {
        return resourceBuilder;
    }

    public List<Snippet> getSnippets() {
        return List.copyOf(snippets);
    }
}
