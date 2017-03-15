import com.liferay.portal.kernel.util.HashCodeFactoryUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.EntityResolver;
import com.liferay.portal.util.HashCodeFactoryImpl;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.parser.DefaultWorkflowValidator;
import com.liferay.portal.workflow.kaleo.parser.XMLWorkflowModelParser;
import com.liferay.portal.xml.SAXReaderImpl;

import java.io.File;
import java.io.FileInputStream;

import org.apache.xerces.parsers.SAXParser;
import org.dom4j.io.SAXReader;


public class WorkflowValidation
{

    static class SAXReaderImplExt extends SAXReaderImpl
    {
        @Override
        protected SAXReader getSAXReader(boolean validate) {
            SAXReader reader;
            try {
                reader = new org.dom4j.io.SAXReader(new SAXParser(), validate);

                reader.setEntityResolver(new EntityResolver());

                reader.setFeature(_FEATURES_DYNAMIC, validate);
                reader.setFeature(_FEATURES_EXTERNAL_GENERAL_ENTITIES, validate);
                reader.setFeature(_FEATURES_LOAD_DTD_GRAMMAR, validate);
                reader.setFeature(_FEATURES_LOAD_EXTERNAL_DTD, validate);
                reader.setFeature(_FEATURES_VALIDATION, validate);
                reader.setFeature(_FEATURES_VALIDATION_SCHEMA, validate);
                reader.setFeature(_FEATURES_VALIDATION_SCHEMA_FULL_CHECKING, validate);
            }
            catch (Exception e) {
                reader = new org.dom4j.io.SAXReader(false);

                reader.setEntityResolver(new EntityResolver());
            }

            return reader;
        }
        private static final String _FEATURES_DYNAMIC =
            "http://apache.org/xml/features/validation/dynamic";

        private static final String _FEATURES_EXTERNAL_GENERAL_ENTITIES =
            "http://xml.org/sax/features/external-general-entities";

        private static final String _FEATURES_LOAD_DTD_GRAMMAR =
            "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";

        private static final String _FEATURES_LOAD_EXTERNAL_DTD =
            "http://apache.org/xml/features/nonvalidating/load-external-dtd";

        private static final String _FEATURES_VALIDATION =
            "http://xml.org/sax/features/validation";

        private static final String _FEATURES_VALIDATION_SCHEMA =
            "http://apache.org/xml/features/validation/schema";

        private static final String _FEATURES_VALIDATION_SCHEMA_FULL_CHECKING =
            "http://apache.org/xml/features/validation/schema-full-checking";
    }

    public Exception validate(InputStream input)
    {
        try {
            new HashCodeFactoryUtil().setHashCodeFactory(new HashCodeFactoryImpl());

            new SAXReaderUtil().setSAXReader(new SAXReaderImplExt());

            XMLWorkflowModelParser _workflowModelParser = new XMLWorkflowModelParser();

            _workflowModelParser.setValidate(false);

            Definition definition = _workflowModelParser.parse(input);

            DefaultWorkflowValidator _workflowValidator = new DefaultWorkflowValidator();

            _workflowValidator.validate(definition);
        }
        catch (Exception e) {
            return e;
        }

        return null;
    }
}
