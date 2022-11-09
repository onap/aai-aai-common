.. contents::
   :depth: 3
..

vnf to esr-system-info Named Query
==================================

**Example Request**

POST /aai/search/named-query

{

"query-parameters": {

"named-query": {

"named-query-uuid": "037eb932-edac-48f5-9782-c19c0aa5a031"

}

},

"instance-filters":{

"instance-filter":[ {

"generic-vnf": {

"vnf-id": "de7cc3ab-0212-47df-9e64-da1c79234deb"

}

} ]

}

}

**Example Response**

{

"inventory-response-item": [

{

"model-name": "service-instance",

"generic-vnf": {

"vnf-id": "de7cc3ab-0212-47df-9e64-da1c79234deb",

"vnf-name": "ZRDM2MMEX39",

"vnf-type": "vMME Svc Jul 14/vMME VF Jul 14 1",

"service-id": "a9a77d5a-123e-4ca2-9eb9-0b015d2ee0fb",

"orchestration-status": "active",

"in-maint": false,

"is-closed-loop-disabled": false,

"resource-version": "1504805258328",

"model-invariant-id": "82194af1-3c2c-485a-8f44-420e22a9eaa4",

"model-version-id": "46b92144-923a-4d20-b85a-3cbd847668a9"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"vserver": {

"vserver-id": "example-vserver-id-val-90579",

"vserver-name": "example-vserver-name-val-92986",

"vserver-name2": "example-vserver-name2-val-77692",

"prov-status": "example-prov-status-val-23854",

"vserver-selflink": "example-vserver-selflink-val-14328",

"in-maint": true,

"is-closed-loop-disabled": true,

"resource-version": "1504808495581"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"tenant": {

"tenant-id": "example-tenant-id-val-89637",

"tenant-name": "example-tenant-name-val-36717",

"resource-version": "1504808495527"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"cloud-region": {

"cloud-owner": "example-cloud-owner-val-44086",

"cloud-region-id": "example-cloud-region-id-val-67393",

"cloud-type": "example-cloud-type-val-13758",

"owner-defined-type": "example-owner-defined-type-val-38571",

"cloud-region-version": "example-cloud-region-version-val-130",

"identity-url": "example-identity-url-val-73825",

"cloud-zone": "example-cloud-zone-val-97510",

"complex-name": "example-complex-name-val-28459",

"sriov-automation": false,

"resource-version": "1504808495472"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"esr-system-info": {

"esr-system-info-id": "example-esr-system-info-id-val-25777",

"system-name": "example-system-name-val-29070",

"type": "example-type-val-85254",

"vendor": "example-vendor-val-94515",

"version": "example-version-val-71880",

"service-url": "example-service-url-val-36193",

"user-name": "example-user-name-val-77399",

"password": "example-password-val-46071",

"system-type": "example-system-type-val-76197",

"protocal": "example-protocal-val-52954",

"ssl-cacert": "example-ssl-cacert-val-75021",

"ssl-insecure": true,

"ip-address": "example-ip-address-val-44431",

"port": "example-port-val-93234",

"cloud-domain": "example-cloud-domain-val-76370",

"default-tenant": "example-tenant-id-val-89637",

"resource-version": "1504808496522"

},

"extra-properties": {}

}

]

}

}

]

}

}

]

}

},

{

"vserver": {

"vserver-id": "example-vserver-id-val-2",

"vserver-name": "example-vserver-name-val-2",

"vserver-name2": "example-vserver-name2-val-2",

"prov-status": "example-prov-status-val-2",

"vserver-selflink": "example-vserver-selflink-val-2",

"in-maint": true,

"is-closed-loop-disabled": true,

"resource-version": "1504817435622"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"tenant": {

"tenant-id": "example-tenant-id-val-2",

"tenant-name": "example-tenant-name-val-2",

"resource-version": "1504817435574"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"cloud-region": {

"cloud-owner": "example-cloud-owner-val-2",

"cloud-region-id": "example-cloud-region-id-val-2",

"cloud-type": "example-cloud-type-val-2",

"owner-defined-type": "example-owner-defined-type-val-2",

"cloud-region-version": "example-cloud-region-version-val-2",

"identity-url": "example-identity-url-val-2",

"cloud-zone": "example-cloud-zone-val-2",

"complex-name": "example-complex-name-val-2",

"sriov-automation": false,

"resource-version": "1504817435502"

},

"extra-properties": {},

"inventory-response-items": {

"inventory-response-item": [

{

"esr-system-info": {

"esr-system-info-id": "example-esr-system-info-id-val-2",

"system-name": "example-system-name-val-2",

"type": "example-type-val-2",

"vendor": "example-vendor-val-2",

"version": "example-version-val-2",

"service-url": "example-service-url-val-2",

"user-name": "example-user-name-val-2",

"password": "example-password-val-2",

"system-type": "example-system-type-val-2",

"protocal": "example-protocal-val-2",

"ssl-cacert": "example-ssl-cacert-val-2",

"ssl-insecure": true,

"ip-address": "example-ip-address-val-2",

"port": "example-port-val-2",

"cloud-domain": "example-cloud-domain-val-2",

"default-tenant": "example-tenant-id-val-2",

"resource-version": "1504817436023"

},

"extra-properties": {}

}

]

}

}

]

}

}

]

}

}

]

}

}

]

}
