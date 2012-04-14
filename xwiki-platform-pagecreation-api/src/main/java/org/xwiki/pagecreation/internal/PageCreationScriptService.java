/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.pagecreation.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.bridge.DocumentModelBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.pagecreation.InputTransformer;
import org.xwiki.pagecreation.WhitespaceHandler;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.XWiki;

/**
 * Script access to page creation.
 * 
 * @version $Id$
 */
@Component("pageCreation")
public class PageCreationScriptService implements ScriptService
{

    /**
     * The name of the XClass representing an XClass page creation information.
     */
    private static final String XWIKI_PAGE_CREATION_CLASS = "XWiki.PageCreation";

    /**
     * The parameter key for the title of the document to create.
     */
    private static final String TITLE_KEY = "title";
    
    /**
     * The parameter key for the parent of the document to create.
     */
    private static final String PARENT_KEY = "parent";

    /**
     * The "document title" argument string representation.
     */
    private static final String DOCUMENT_ARGUMENT_TITLE = "document:title";

    /**
     * The "object property" argument string prefix.
     */
    private static final String PROPERTY_ARGUMENT_PREFIX = "property:";

    /**
     * The "instances counter" special argument string representation.
     */
    private static final String SPECIAL_ARGUMENT_COUNTER = "special:counter";

    /**
     * The "current date" special argument string reprensentation.
     */
    private static final String SPECIAL_ARGUMENT_DATE = "special:date";

    /**
     * The bearded one with the checkered shirt.
     */
    @Inject
    private Logger logger;

    /**
     * Access bridge. Used to access document, object, properties.
     */
    @Inject
    private DocumentAccessBridge bridge;

    /**
     * Execution. Used to access the legacy XWiki context and APIs.
     */
    @Inject
    private Execution execution;

    /**
     * Reference serializer that omits the wiki part.
     */
    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> serializer;

    /**
     * All input transformers available.
     */
    @Inject
    private Map<String, InputTransformer> inputTransformers;

    /**
     * All whitespace handlers available.
     */
    @Inject
    private Map<String, WhitespaceHandler> whitespaceHandler;

    /**
     * Creates the document for the passed classed, based on the passed parameters.
     * 
     * @param classDocumentReference the class document of the page to create.
     * @param parameters the creation parameters. Usually will come form a request parameter map.
     * @return a pair of strings that indicated the status (first one) and a message (second one) of the result of the
     *         operation.
     */
    public String[] create(DocumentReference classDocumentReference, Map<String, String[]> parameters)
    {
        XWiki api = getXWikiApi();

        try {
            String targetName = this.computeName(classDocumentReference, parameters);
            String title = this.computeTitle(classDocumentReference, parameters);

            Document targetDocument = api.getDocument(targetName);

            targetDocument.addObjectFromRequest(this.serializer.serialize(classDocumentReference));
            targetDocument.setTitle(title);
            String parent = "";
            if (parameters.containsKey(PARENT_KEY)) {
                parent = parameters.get(PARENT_KEY)[0];
            }
            targetDocument.setParent(parent);

            targetDocument.save();

            return new String[] {"OK", targetDocument.getURL("view")};

        } catch (XWikiException e) {

            this.logger.error("Error creating document", e);
            return new String[] {"ERROR_UNKNOWN", e.getMessage()};
        }

    }

    /**
     * Computes the title for the passed class document and parameters.
     * 
     * @param classDocumentReference the class of the object for the target page to create
     * @param parameters the creation parameters
     * @return the computed future page title
     */
    public String computeTitle(DocumentReference classDocumentReference, Map<String, String[]> parameters)
    {
        try {
            DocumentModelBridge classDocument = bridge.getDocument(classDocumentReference);

            String titleFormat =
                (String) bridge.getProperty(classDocument.getFullName(), XWIKI_PAGE_CREATION_CLASS, "titleFormat");

            return this.computeString(classDocument, parameters, titleFormat);
        } catch (Exception e) {
            this.logger.warn("Failed to compute title preview for document.", e);
            return null;
        }
    }

    /**
     * Computes the page name (last segment of the URL) for the passed class document and parameters.
     * 
     * @param classDocumentReference the class of the object for the target page to create
     * @param parameters the creation parameters
     * @return the computed future page name
     */
    public String computeName(DocumentReference classDocumentReference, Map<String, String[]> parameters)
    {
        try {
            DocumentModelBridge classDocument = bridge.getDocument(classDocumentReference);

            String nameFormat =
                (String) bridge.getProperty(classDocument.getFullName(), XWIKI_PAGE_CREATION_CLASS, "nameFormat");

            return this.computeString(classDocument, parameters, nameFormat);
        } catch (Exception e) {
            this.logger.warn("Failed to compute name preview for document.", e);
            return null;
        }
    }

    /**
     * Private helper to compute string based on a format and creation parameters.
     * 
     * @param classDocument the class of the object for the target page to create
     * @param parameters the creation parameters
     * @param format the format of the string to create
     * @return the formated string
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    private String computeString(DocumentModelBridge classDocument, Map<String, String[]> parameters, String format)
    {

        String[] arguments =
            StringUtils.split(
                (String) bridge.getProperty(classDocument.getFullName(), XWIKI_PAGE_CREATION_CLASS, "arguments"), null);

        List<String> nameTransformations =
            (List<String>) bridge.getProperty(classDocument.getFullName(), XWIKI_PAGE_CREATION_CLASS,
                "nameTransformations");

        String nameWhitespaceStrategy =
            (String) bridge.getProperty(classDocument.getFullName(), XWIKI_PAGE_CREATION_CLASS,
                "nameWhitespaceStrategy");

        List<Object> args = this.getArguments(arguments, classDocument, nameTransformations, parameters);

        String result = String.format(format, args.toArray());

        if (this.whitespaceHandler.containsKey(nameWhitespaceStrategy)) {
            result = this.whitespaceHandler.get(nameWhitespaceStrategy).handle(result);
        }

        return result;

    }

    /**
     * Private helper to construct the list of arguments based on some parameters.
     * 
     * @param arguments the XWiki string representation of the arguments
     * @param classDocument the class document of the object to create in the new page
     * @param nameTransformations the transformations to apply to user input
     * @param parameters the parameter map
     * @return the list of java object arguments
     */
    private List<Object> getArguments(String[] arguments, DocumentModelBridge classDocument,
        List<String> nameTransformations, Map<String, String[]> parameters)
    {
        List<Object> args = new ArrayList<Object>();
        for (String argument : arguments) {
            if (argument.startsWith(PROPERTY_ARGUMENT_PREFIX)) {
                String property = StringUtils.substringAfter(argument, PROPERTY_ARGUMENT_PREFIX);
                String key = classDocument.getFullName() + "_0_" + property;
                String input =
                    StringUtils.defaultIfEmpty(parameters.containsKey(key) ? parameters.get(key)[0] : "", "");

                for (String transformation : nameTransformations) {
                    if (this.inputTransformers.containsKey(transformation)) {
                        input = this.inputTransformers.get(transformation).transform(input);
                    }
                }

                args.add(input);
            } else if (argument.equals(DOCUMENT_ARGUMENT_TITLE)) {
                args.add(parameters.containsKey(TITLE_KEY) ? parameters.get(TITLE_KEY)[0] : "");
            } else if (argument.equals(SPECIAL_ARGUMENT_DATE)) {
                args.add(new Date());
            } else if (argument.equals(SPECIAL_ARGUMENT_COUNTER)) {
                args.add(42);
            }
        }
        return args;
    }

    /**
     * Accessor to the XWiki API.
     * 
     * @return the XWiki api object
     */
    private XWiki getXWikiApi()
    {
        XWikiContext context = (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
        return new XWiki(context.getWiki(), context);
    }
}
