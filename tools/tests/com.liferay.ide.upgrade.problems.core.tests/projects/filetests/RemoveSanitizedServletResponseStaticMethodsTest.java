
public class RemoveSanitizedServletResponseStaticMethodsTest {

	@Test
	public void testInvocationMethod() throws Exception {

		SanitizedServletResponse.disableXSSAuditor(_httpServletResponse);
		
		SanitizedServletResponse.disableXSSAuditor(_portletResponse)
		
		SanitizedServletResponse.disableXSSAuditorOnNextRequest(_httpServletRequest)
		
		SanitizedServletResponse.disableXSSAuditorOnNextRequest(_portletRequest)
	}
	
	@Reference
	private HttpServletResponse _httpServletResponse;
	
	@Reference
	private PortletResponse _portletResponse;
	
	@Reference
	private HttpServletRequest _httpServletRequest;
	
	@Reference
	private PortletRequest _portletRequest;

}
