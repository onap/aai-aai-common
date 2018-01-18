{
	"rules": [
<#list wrappedRules as rule>
<#include "ruleTemplate.ftlh">
<#if rule_has_next>,</#if>
	</#list>
	]
}