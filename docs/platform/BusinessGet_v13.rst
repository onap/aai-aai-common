Active and Available Inventory REST API v13
===========================================

.. toctree::
    :maxdepth: 3


Description
~~~~~~~~~~~


[Differences versus the previous schema version](apidocs/aai_swagger_v13.diff)

Copyright &copy; 2017 AT&amp;T Intellectual Property. All rights reserved.

Licensed under the Creative Commons License, Attribution 4.0 Intl. (the &quot;License&quot;); you may not use this documentation except in compliance with the License.

You may obtain a copy of the License at

(https://creativecommons.org/licenses/by/4.0/)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

ECOMP and OpenECOMP are trademarks and service marks of AT&amp;T Intellectual Property.

This document is best viewed with Firefox or Chrome. Nodes can be found by appending /#/definitions/node-type-to-find to the path to this document. Edge definitions can be found with the node definitions.




Contact Information
~~~~~~~~~~~~~~~~~~~


None



None



None




License
~~~~~~~


`Apache 2.0 <http://www.apache.org/licenses/LICENSE-2.0.html>`_




Base URL
~~~~~~~~

https://None/aai/v13

BUSINESS
~~~~~~~~




GET ``/business/connectors``
----------------------------


Summary
+++++++

returns connectors

Description
+++++++++++

.. raw:: html

    returns connectors

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        resource-instance-id | query | No | string |  |  | None
        model-invariant-id | query | No | string |  |  | None
        model-version-id | query | No | string |  |  | None
        widget-model-id | query | No | string |  |  | None
        widget-model-version | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`connectors <d_869f99b5faa8cbdbd94b79f9723e3f50>`

**Example:**

.. code-block:: javascript

    {
        "connector": [
            {
                "metadata": [
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-instance-id": "somestring",
                "resource-version": "somestring",
                "widget-model-id": "somestring",
                "widget-model-version": "somestring"
            },
            {
                "metadata": [
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-instance-id": "somestring",
                "resource-version": "somestring",
                "widget-model-id": "somestring",
                "widget-model-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/connectors/connector/{resource-instance-id}``
-------------------------------------------------------------


Summary
+++++++

returns connector

Description
+++++++++++

.. raw:: html

    returns connector

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        resource-instance-id | path | Yes | string |  |  | Unique id of resource instance.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`connector <d_92a3e20a1b37b23d3bff53fd05390d84>`

**Example:**

.. code-block:: javascript

    {
        "metadata": [
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            },
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            }
        ],
        "model-invariant-id": "somestring",
        "model-version-id": "somestring",
        "persona-model-version": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-instance-id": "somestring",
        "resource-version": "somestring",
        "widget-model-id": "somestring",
        "widget-model-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/connectors/connector/{resource-instance-id}/metadata``
----------------------------------------------------------------------


Summary
+++++++

returns metadata

Description
+++++++++++

.. raw:: html

    returns metadata

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        resource-instance-id | path | Yes | string |  |  | Unique id of resource instance.
        metaname | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`metadata <d_34f85031375a8205fdb27ce838465ed8>`

**Example:**

.. code-block:: javascript

    {
        "metadatum": [
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            },
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/connectors/connector/{resource-instance-id}/metadata/metadatum/{metaname}``
-------------------------------------------------------------------------------------------


Summary
+++++++

returns metadatum

Description
+++++++++++

.. raw:: html

    returns metadatum

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        resource-instance-id | path | Yes | string |  |  | Unique id of resource instance.
        metaname | path | Yes | string |  |  | 


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>`

**Example:**

.. code-block:: javascript

    {
        "metaname": "somestring",
        "metaval": "somestring",
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers``
---------------------------


Summary
+++++++

returns customers

Description
+++++++++++

.. raw:: html

    returns customers

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | query | No | string |  |  | None
        subscriber-name | query | No | string |  |  | None
        subscriber-type | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`customers <d_c31886502956929aa4378077faf41c64>`

**Example:**

.. code-block:: javascript

    {
        "customer": [
            {
                "global-customer-id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "service-subscriptions": [
                    {
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "service-instances": [
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            },
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            }
                        ],
                        "service-type": "somestring",
                        "temp-ub-sub-account-id": "somestring"
                    },
                    {
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "service-instances": [
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            },
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            }
                        ],
                        "service-type": "somestring",
                        "temp-ub-sub-account-id": "somestring"
                    }
                ],
                "subscriber-name": "somestring",
                "subscriber-type": "somestring"
            },
            {
                "global-customer-id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "service-subscriptions": [
                    {
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "service-instances": [
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            },
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            }
                        ],
                        "service-type": "somestring",
                        "temp-ub-sub-account-id": "somestring"
                    },
                    {
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "service-instances": [
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            },
                            {
                                "allotted-resources": [
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    },
                                    {
                                        "description": "somestring",
                                        "id": "somestring",
                                        "model-invariant-id": "somestring",
                                        "model-version-id": "somestring",
                                        "operational-status": "somestring",
                                        "orchestration-status": "somestring",
                                        "persona-model-version": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring",
                                        "role": "somestring",
                                        "selflink": "somestring",
                                        "tunnel-xconnects": [
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            },
                                            {
                                                "bandwidth-down-wan1": "somestring",
                                                "bandwidth-down-wan2": "somestring",
                                                "bandwidth-up-wan1": "somestring",
                                                "bandwidth-up-wan2": "somestring",
                                                "id": "somestring",
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    },
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "related-to-property": [
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            },
                                                            {
                                                                "property-key": "somestring",
                                                                "property-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-data": [
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            },
                                                            {
                                                                "relationship-key": "somestring",
                                                                "relationship-value": "somestring"
                                                            }
                                                        ],
                                                        "relationship-label": "somestring"
                                                    }
                                                ],
                                                "resource-version": "somestring"
                                            }
                                        ],
                                        "type": "somestring"
                                    }
                                ],
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-total": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "environment-context": "somestring",
                                "metadata": [
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "metaname": "somestring",
                                        "metaval": "somestring",
                                        "resource-version": "somestring"
                                    }
                                ],
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "selflink": "somestring",
                                "service-instance-id": "somestring",
                                "service-instance-location-id": "somestring",
                                "service-instance-name": "somestring",
                                "service-role": "somestring",
                                "service-type": "somestring",
                                "vhn-portal-url": "somestring",
                                "widget-model-id": "somestring",
                                "widget-model-version": "somestring",
                                "workload-context": "somestring"
                            }
                        ],
                        "service-type": "somestring",
                        "temp-ub-sub-account-id": "somestring"
                    }
                ],
                "subscriber-name": "somestring",
                "subscriber-type": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}``
---------------------------------------------------------


Summary
+++++++

returns customer

Description
+++++++++++

.. raw:: html

    returns customer

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`customer <d_83358d13f308023dd91442690cc6662b>`

**Example:**

.. code-block:: javascript

    {
        "global-customer-id": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring",
        "service-subscriptions": [
            {
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "service-instances": [
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    },
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    }
                ],
                "service-type": "somestring",
                "temp-ub-sub-account-id": "somestring"
            },
            {
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "service-instances": [
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    },
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    }
                ],
                "service-type": "somestring",
                "temp-ub-sub-account-id": "somestring"
            }
        ],
        "subscriber-name": "somestring",
        "subscriber-type": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions``
-------------------------------------------------------------------------------


Summary
+++++++

returns service-subscriptions

Description
+++++++++++

.. raw:: html

    returns service-subscriptions

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`service-subscriptions <d_f60fad7ffa7385f016efae73cd634c31>`

**Example:**

.. code-block:: javascript

    {
        "service-subscription": [
            {
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "service-instances": [
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    },
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    }
                ],
                "service-type": "somestring",
                "temp-ub-sub-account-id": "somestring"
            },
            {
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "service-instances": [
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    },
                    {
                        "allotted-resources": [
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            },
                            {
                                "description": "somestring",
                                "id": "somestring",
                                "model-invariant-id": "somestring",
                                "model-version-id": "somestring",
                                "operational-status": "somestring",
                                "orchestration-status": "somestring",
                                "persona-model-version": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring",
                                "role": "somestring",
                                "selflink": "somestring",
                                "tunnel-xconnects": [
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    },
                                    {
                                        "bandwidth-down-wan1": "somestring",
                                        "bandwidth-down-wan2": "somestring",
                                        "bandwidth-up-wan1": "somestring",
                                        "bandwidth-up-wan2": "somestring",
                                        "id": "somestring",
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            },
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "related-to-property": [
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    },
                                                    {
                                                        "property-key": "somestring",
                                                        "property-value": "somestring"
                                                    }
                                                ],
                                                "relationship-data": [
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    },
                                                    {
                                                        "relationship-key": "somestring",
                                                        "relationship-value": "somestring"
                                                    }
                                                ],
                                                "relationship-label": "somestring"
                                            }
                                        ],
                                        "resource-version": "somestring"
                                    }
                                ],
                                "type": "somestring"
                            }
                        ],
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-total": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "environment-context": "somestring",
                        "metadata": [
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "metaname": "somestring",
                                "metaval": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "selflink": "somestring",
                        "service-instance-id": "somestring",
                        "service-instance-location-id": "somestring",
                        "service-instance-name": "somestring",
                        "service-role": "somestring",
                        "service-type": "somestring",
                        "vhn-portal-url": "somestring",
                        "widget-model-id": "somestring",
                        "widget-model-version": "somestring",
                        "workload-context": "somestring"
                    }
                ],
                "service-type": "somestring",
                "temp-ub-sub-account-id": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}``
-------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns service-subscription

Description
+++++++++++

.. raw:: html

    returns service-subscription

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`service-subscription <d_11c1b150c0c2ac297721ad7dedd11ff0>`

**Example:**

.. code-block:: javascript

    {
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring",
        "service-instances": [
            {
                "allotted-resources": [
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    },
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    }
                ],
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-total": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "environment-context": "somestring",
                "metadata": [
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "selflink": "somestring",
                "service-instance-id": "somestring",
                "service-instance-location-id": "somestring",
                "service-instance-name": "somestring",
                "service-role": "somestring",
                "service-type": "somestring",
                "vhn-portal-url": "somestring",
                "widget-model-id": "somestring",
                "widget-model-version": "somestring",
                "workload-context": "somestring"
            },
            {
                "allotted-resources": [
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    },
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    }
                ],
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-total": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "environment-context": "somestring",
                "metadata": [
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "selflink": "somestring",
                "service-instance-id": "somestring",
                "service-instance-location-id": "somestring",
                "service-instance-name": "somestring",
                "service-role": "somestring",
                "service-type": "somestring",
                "vhn-portal-url": "somestring",
                "widget-model-id": "somestring",
                "widget-model-version": "somestring",
                "workload-context": "somestring"
            }
        ],
        "service-type": "somestring",
        "temp-ub-sub-account-id": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances``
-------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns service-instances

Description
+++++++++++

.. raw:: html

    returns service-instances

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | query | No | string |  |  | None
        service-instance-name | query | No | string |  |  | None
        environment-context | query | No | string |  |  | None
        workload-context | query | No | string |  |  | None
        model-invariant-id | query | No | string |  |  | None
        model-version-id | query | No | string |  |  | None
        widget-model-id | query | No | string |  |  | None
        widget-model-version | query | No | string |  |  | None
        service-instance-location-id | query | No | string |  |  | None
        orchestration-status | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`service-instances <d_6f012b6817169c6f77c16b1bf155d09c>`

**Example:**

.. code-block:: javascript

    {
        "service-instance": [
            {
                "allotted-resources": [
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    },
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    }
                ],
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-total": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "environment-context": "somestring",
                "metadata": [
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "selflink": "somestring",
                "service-instance-id": "somestring",
                "service-instance-location-id": "somestring",
                "service-instance-name": "somestring",
                "service-role": "somestring",
                "service-type": "somestring",
                "vhn-portal-url": "somestring",
                "widget-model-id": "somestring",
                "widget-model-version": "somestring",
                "workload-context": "somestring"
            },
            {
                "allotted-resources": [
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    },
                    {
                        "description": "somestring",
                        "id": "somestring",
                        "model-invariant-id": "somestring",
                        "model-version-id": "somestring",
                        "operational-status": "somestring",
                        "orchestration-status": "somestring",
                        "persona-model-version": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring",
                        "role": "somestring",
                        "selflink": "somestring",
                        "tunnel-xconnects": [
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            },
                            {
                                "bandwidth-down-wan1": "somestring",
                                "bandwidth-down-wan2": "somestring",
                                "bandwidth-up-wan1": "somestring",
                                "bandwidth-up-wan2": "somestring",
                                "id": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    },
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "related-to-property": [
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            },
                                            {
                                                "property-key": "somestring",
                                                "property-value": "somestring"
                                            }
                                        ],
                                        "relationship-data": [
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            },
                                            {
                                                "relationship-key": "somestring",
                                                "relationship-value": "somestring"
                                            }
                                        ],
                                        "relationship-label": "somestring"
                                    }
                                ],
                                "resource-version": "somestring"
                            }
                        ],
                        "type": "somestring"
                    }
                ],
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-total": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "environment-context": "somestring",
                "metadata": [
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "metaname": "somestring",
                        "metaval": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "selflink": "somestring",
                "service-instance-id": "somestring",
                "service-instance-location-id": "somestring",
                "service-instance-name": "somestring",
                "service-role": "somestring",
                "service-type": "somestring",
                "vhn-portal-url": "somestring",
                "widget-model-id": "somestring",
                "widget-model-version": "somestring",
                "workload-context": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns service-instance

Description
+++++++++++

.. raw:: html

    returns service-instance

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`service-instance <d_4dece517d5f6a71e0094e07bca14006b>`

**Example:**

.. code-block:: javascript

    {
        "allotted-resources": [
            {
                "description": "somestring",
                "id": "somestring",
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "operational-status": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "role": "somestring",
                "selflink": "somestring",
                "tunnel-xconnects": [
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    },
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    }
                ],
                "type": "somestring"
            },
            {
                "description": "somestring",
                "id": "somestring",
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "operational-status": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "role": "somestring",
                "selflink": "somestring",
                "tunnel-xconnects": [
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    },
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    }
                ],
                "type": "somestring"
            }
        ],
        "bandwidth-down-wan1": "somestring",
        "bandwidth-down-wan2": "somestring",
        "bandwidth-total": "somestring",
        "bandwidth-up-wan1": "somestring",
        "bandwidth-up-wan2": "somestring",
        "environment-context": "somestring",
        "metadata": [
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            },
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            }
        ],
        "model-invariant-id": "somestring",
        "model-version-id": "somestring",
        "orchestration-status": "somestring",
        "persona-model-version": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring",
        "selflink": "somestring",
        "service-instance-id": "somestring",
        "service-instance-location-id": "somestring",
        "service-instance-name": "somestring",
        "service-role": "somestring",
        "service-type": "somestring",
        "vhn-portal-url": "somestring",
        "widget-model-id": "somestring",
        "widget-model-version": "somestring",
        "workload-context": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/allotted-resources``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns allotted-resources

Description
+++++++++++

.. raw:: html

    returns allotted-resources

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service
        id | query | No | string |  |  | None
        model-invariant-id | query | No | string |  |  | None
        model-version-id | query | No | string |  |  | None
        type | query | No | string |  |  | None
        role | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`allotted-resources <d_25340b16d75c192353ce0cd0651c1005>`

**Example:**

.. code-block:: javascript

    {
        "allotted-resource": [
            {
                "description": "somestring",
                "id": "somestring",
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "operational-status": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "role": "somestring",
                "selflink": "somestring",
                "tunnel-xconnects": [
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    },
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    }
                ],
                "type": "somestring"
            },
            {
                "description": "somestring",
                "id": "somestring",
                "model-invariant-id": "somestring",
                "model-version-id": "somestring",
                "operational-status": "somestring",
                "orchestration-status": "somestring",
                "persona-model-version": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring",
                "role": "somestring",
                "selflink": "somestring",
                "tunnel-xconnects": [
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    },
                    {
                        "bandwidth-down-wan1": "somestring",
                        "bandwidth-down-wan2": "somestring",
                        "bandwidth-up-wan1": "somestring",
                        "bandwidth-up-wan2": "somestring",
                        "id": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            },
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "related-to-property": [
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    },
                                    {
                                        "property-key": "somestring",
                                        "property-value": "somestring"
                                    }
                                ],
                                "relationship-data": [
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    },
                                    {
                                        "relationship-key": "somestring",
                                        "relationship-value": "somestring"
                                    }
                                ],
                                "relationship-label": "somestring"
                            }
                        ],
                        "resource-version": "somestring"
                    }
                ],
                "type": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/allotted-resources/allotted-resource/{id}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns allotted-resource

Description
+++++++++++

.. raw:: html

    returns allotted-resource

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service
        id | path | Yes | string |  |  | Allotted Resource id UUID assigned to this instance.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`allotted-resource <d_74e3067c98649f9bd125a1f12efff94c>`

**Example:**

.. code-block:: javascript

    {
        "description": "somestring",
        "id": "somestring",
        "model-invariant-id": "somestring",
        "model-version-id": "somestring",
        "operational-status": "somestring",
        "orchestration-status": "somestring",
        "persona-model-version": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring",
        "role": "somestring",
        "selflink": "somestring",
        "tunnel-xconnects": [
            {
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            },
            {
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            }
        ],
        "type": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/allotted-resources/allotted-resource/{id}/tunnel-xconnects``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns tunnel-xconnects

Description
+++++++++++

.. raw:: html

    returns tunnel-xconnects

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service
        id | path | Yes | string |  |  | Allotted Resource id UUID assigned to this instance.
        id | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`tunnel-xconnects <d_826c51b048c1d852486a2752adea1274>`

**Example:**

.. code-block:: javascript

    {
        "tunnel-xconnect": [
            {
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            },
            {
                "bandwidth-down-wan1": "somestring",
                "bandwidth-down-wan2": "somestring",
                "bandwidth-up-wan1": "somestring",
                "bandwidth-up-wan2": "somestring",
                "id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/allotted-resources/allotted-resource/{id}/tunnel-xconnects/tunnel-xconnect/{id}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns tunnel-xconnect

Description
+++++++++++

.. raw:: html

    returns tunnel-xconnect

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service
        id | path | Yes | string |  |  | Allotted Resource id UUID assigned to this instance.
        id | path | Yes | string |  |  | Allotted Resource id UUID assigned to this instance.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`tunnel-xconnect <d_85f7215a4cda082db6705b64e5dbf328>`

**Example:**

.. code-block:: javascript

    {
        "bandwidth-down-wan1": "somestring",
        "bandwidth-down-wan2": "somestring",
        "bandwidth-up-wan1": "somestring",
        "bandwidth-up-wan2": "somestring",
        "id": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/metadata``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns metadata

Description
+++++++++++

.. raw:: html

    returns metadata

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service
        metaname | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`metadata <d_34f85031375a8205fdb27ce838465ed8>`

**Example:**

.. code-block:: javascript

    {
        "metadatum": [
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            },
            {
                "metaname": "somestring",
                "metaval": "somestring",
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}/service-instances/service-instance/{service-instance-id}/metadata/metadatum/{metaname}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

returns metadatum

Description
+++++++++++

.. raw:: html

    returns metadatum

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        global-customer-id | path | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        service-type | path | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        service-instance-id | path | Yes | string |  |  | Uniquely identifies this instance of a service
        metaname | path | Yes | string |  |  | 


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>`

**Example:**

.. code-block:: javascript

    {
        "metaname": "somestring",
        "metaval": "somestring",
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/lines-of-business``
-----------------------------------


Summary
+++++++

returns lines-of-business

Description
+++++++++++

.. raw:: html

    returns lines-of-business

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        line-of-business-name | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`lines-of-business <d_9211f47aafde6f1f8ccf5f8074a9874c>`

**Example:**

.. code-block:: javascript

    {
        "line-of-business": [
            {
                "line-of-business-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            },
            {
                "line-of-business-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/lines-of-business/line-of-business/{line-of-business-name}``
----------------------------------------------------------------------------


Summary
+++++++

returns line-of-business

Description
+++++++++++

.. raw:: html

    returns line-of-business

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        line-of-business-name | path | Yes | string |  |  | Name of the line-of-business (product)


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`line-of-business <d_0f32266df994400cd3d56dff29982dba>`

**Example:**

.. code-block:: javascript

    {
        "line-of-business-name": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/owning-entities``
---------------------------------


Summary
+++++++

returns owning-entities

Description
+++++++++++

.. raw:: html

    returns owning-entities

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        owning-entity-id | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`owning-entities <d_805f2006ebd3dc91d28d22a6cfd884a9>`

**Example:**

.. code-block:: javascript

    {
        "owning-entity": [
            {
                "owning-entity-id": "somestring",
                "owning-entity-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            },
            {
                "owning-entity-id": "somestring",
                "owning-entity-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/owning-entities/owning-entity/{owning-entity-id}``
------------------------------------------------------------------


Summary
+++++++

returns owning-entity

Description
+++++++++++

.. raw:: html

    returns owning-entity

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        owning-entity-id | path | Yes | string |  |  | UUID of an owning entity


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`owning-entity <d_24a2377ce1bd600697c2d2dd58624524>`

**Example:**

.. code-block:: javascript

    {
        "owning-entity-id": "somestring",
        "owning-entity-name": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/platforms``
---------------------------


Summary
+++++++

returns platforms

Description
+++++++++++

.. raw:: html

    returns platforms

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        platform-name | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`platforms <d_0ffb5651bff9e2827913c502ef1105ff>`

**Example:**

.. code-block:: javascript

    {
        "platform": [
            {
                "platform-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            },
            {
                "platform-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/platforms/platform/{platform-name}``
----------------------------------------------------


Summary
+++++++

returns platform

Description
+++++++++++

.. raw:: html

    returns platform

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        platform-name | path | Yes | string |  |  | Name of the platform


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`platform <d_c576aa1c4238465fa72af06f96b4feef>`

**Example:**

.. code-block:: javascript

    {
        "platform-name": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/projects``
--------------------------


Summary
+++++++

returns projects

Description
+++++++++++

.. raw:: html

    returns projects

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        project-name | query | No | string |  |  | None


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`projects <d_2c4eaf0d859776872b364ba279823a75>`

**Example:**

.. code-block:: javascript

    {
        "project": [
            {
                "project-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            },
            {
                "project-name": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    },
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "related-to-property": [
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            },
                            {
                                "property-key": "somestring",
                                "property-value": "somestring"
                            }
                        ],
                        "relationship-data": [
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            },
                            {
                                "relationship-key": "somestring",
                                "relationship-value": "somestring"
                            }
                        ],
                        "relationship-label": "somestring"
                    }
                ],
                "resource-version": "somestring"
            }
        ]
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






GET ``/business/projects/project/{project-name}``
-------------------------------------------------


Summary
+++++++

returns project

Description
+++++++++++

.. raw:: html

    returns project

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        project-name | path | Yes | string |  |  | Name of the project deploying a service


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`project <d_e9de9cd8a8cd53a8e8ccbab0a89a3bf0>`

**Example:**

.. code-block:: javascript

    {
        "project-name": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            },
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "related-to-property": [
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    },
                    {
                        "property-key": "somestring",
                        "property-value": "somestring"
                    }
                ],
                "relationship-data": [
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    },
                    {
                        "relationship-key": "somestring",
                        "relationship-value": "somestring"
                    }
                ],
                "relationship-label": "somestring"
            }
        ],
        "resource-version": "somestring"
    }

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).




  
Data Structures
~~~~~~~~~~~~~~~

.. _d_74e3067c98649f9bd125a1f12efff94c:

allotted-resource Model Structure
---------------------------------

Represents a slice or partial piece of a resource that gets separately allotted
###### Related Nodes
- TO allotted-resource( allotted-resource BindsTo allotted-resource, ONE2ONE, will delete target node)
An edge comment
- TO generic-vnf( allotted-resource PartOf generic-vnf, MANY2MANY, will delete target node)
- TO instance-group( allotted-resource MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( allotted-resource PartOf l3-network, MANY2MANY, will delete target node)
- TO l-interface( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- TO network-policy( allotted-resource Uses network-policy, ONE2ONE, will delete target node)
- TO vlan( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- TO vpn-binding( allotted-resource BelongsTo vpn-binding, MANY2MANY, will delete target node)
- TO service-instance( allotted-resource BelongsTo service-instance, MANY2ONE, will delete target node)(4)
- TO model-ver( allotted-resource IsA model-ver, Many2One, will delete target node)
- FROM tunnel-xconnect( tunnel-xconnect BelongsTo allotted-resource, ONE2ONE, will delete target node)(1)
- FROM configuration( configuration Uses allotted-resource, ONE2ONE, will delete target node)(3)
- FROM service-instance( service-instance Uses allotted-resource, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource BindsTo allotted-resource, ONE2ONE, will delete target node)
An edge comment

-(1) IF this ALLOTTED-RESOURCE node is deleted, this FROM node is DELETED also
-(3) IF this FROM node is deleted, this ALLOTTED-RESOURCE is DELETED also
-(4) IF this TO node is deleted, this ALLOTTED-RESOURCE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        description | No | string |  |  | The descriptive information assigned to this allotted resource instance
        id | Yes | string |  |  | Allotted Resource id UUID assigned to this instance.
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational
        orchestration-status | No | string |  |  | Orchestration status
        persona-model-version | No | string |  |  | the ASDC model version for this resource or service model.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Concurrency value
        role | No | string |  |  | role in the network that this resource will be providing.
        selflink | No | string |  |  | Link back to more information in the controller
        tunnel-xconnects | No | array of :ref:`tunnel-xconnect <d_85f7215a4cda082db6705b64e5dbf328>` |  |  | 
        type | No | string |  |  | Generic description of the type of allotted resource.

.. _d_25340b16d75c192353ce0cd0651c1005:

allotted-resources Model Structure
----------------------------------

This object is used to store slices of services being offered


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        allotted-resource | No | array of :ref:`allotted-resource <d_74e3067c98649f9bd125a1f12efff94c>` |  |  | 

.. _d_a1dcb5379e85a7ecc7fd22802204694d:

business Model Structure
------------------------

Namespace for business related constructs


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        connectors | No | array of :ref:`connector <d_92a3e20a1b37b23d3bff53fd05390d84>` |  |  | 
        customers | No | array of :ref:`customer <d_83358d13f308023dd91442690cc6662b>` |  |  | 
        lines-of-business | No | array of :ref:`line-of-business <d_0f32266df994400cd3d56dff29982dba>` |  |  | 
        owning-entities | No | array of :ref:`owning-entity <d_24a2377ce1bd600697c2d2dd58624524>` |  |  | 
        platforms | No | array of :ref:`platform <d_c576aa1c4238465fa72af06f96b4feef>` |  |  | 
        projects | No | array of :ref:`project <d_e9de9cd8a8cd53a8e8ccbab0a89a3bf0>` |  |  | 

.. _d_92a3e20a1b37b23d3bff53fd05390d84:

connector Model Structure
-------------------------

Collection of resource instances used to connect a variety of disparate inventory widgets
###### Related Nodes
- TO virtual-data-center( connector LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- FROM metadatum( metadatum BelongsTo connector, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance Uses connector, MANY2MANY, will delete target node)

-(1) IF this CONNECTOR node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        metadata | No | array of :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>` |  |  | 
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        persona-model-version | No | string |  |  | the ASDC model version for this resource or service model.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-instance-id | Yes | string |  |  | Unique id of resource instance.
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        widget-model-id | No | string |  |  | the ASDC data dictionary widget model. This maps directly to the A&AI widget.
        widget-model-version | No | string |  |  | the ASDC data dictionary version of the widget model.This maps directly to the A&AI version of the widget.

.. _d_869f99b5faa8cbdbd94b79f9723e3f50:

connectors Model Structure
--------------------------

Collection of resource instances used to connect a variety of disparate inventory widgets


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        connector | No | array of :ref:`connector <d_92a3e20a1b37b23d3bff53fd05390d84>` |  |  | 

.. _d_83358d13f308023dd91442690cc6662b:

customer Model Structure
------------------------

customer identifiers to provide linkage back to BSS information.
###### Related Nodes
- FROM service-subscription( service-subscription BelongsTo customer, MANY2ONE, will delete target node)(1)
- FROM vpn-binding( vpn-binding Uses customer, MANY2MANY, will delete target node)

-(1) IF this CUSTOMER node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        global-customer-id | Yes | string |  |  | Global customer id used across ECOMP to uniquely identify customer.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        service-subscriptions | No | array of :ref:`service-subscription <d_11c1b150c0c2ac297721ad7dedd11ff0>` |  |  | 
        subscriber-name | Yes | string |  |  | Subscriber name, an alternate way to retrieve a customer.
        subscriber-type | Yes | string |  |  | Subscriber type, a way to provide VID with only the INFRA customers.

.. _d_c31886502956929aa4378077faf41c64:

customers Model Structure
-------------------------

Collection of customer identifiers to provide linkage back to BSS information.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        customer | No | array of :ref:`customer <d_83358d13f308023dd91442690cc6662b>` |  |  | 

.. _d_0f32266df994400cd3d56dff29982dba:

line-of-business Model Structure
--------------------------------

describes a line-of-business
###### Related Nodes
- TO generic-vnf( line-of-business Uses generic-vnf, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        line-of-business-name | Yes | string |  |  | Name of the line-of-business (product)
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_9211f47aafde6f1f8ccf5f8074a9874c:

lines-of-business Model Structure
---------------------------------

Collection of lines-of-business


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        line-of-business | No | array of :ref:`line-of-business <d_0f32266df994400cd3d56dff29982dba>` |  |  | 

.. _d_34f85031375a8205fdb27ce838465ed8:

metadata Model Structure
------------------------

Collection of metadatum (key/value pairs)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        metadatum | No | array of :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>` |  |  | 

.. _d_86c5a7078292838659223f545f7cca0a:

metadatum Model Structure
-------------------------

Key/value pairs
###### Related Nodes
- TO configuration( metadatum BelongsTo configuration, MANY2ONE, will delete target node)(4)
- TO connector( metadatum BelongsTo connector, MANY2ONE, will delete target node)(4)
- TO image( metadatum BelongsTo image, MANY2ONE, will delete target node)(4)
- TO model-ver( metadatum BelongsTo model-ver, MANY2ONE, will delete target node)(4)
- TO service-instance( metadatum BelongsTo service-instance, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this METADATUM is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        metaname | Yes | string |  |  | 
        metaval | Yes | string |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_805f2006ebd3dc91d28d22a6cfd884a9:

owning-entities Model Structure
-------------------------------

Collection of owning-entities


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        owning-entity | No | array of :ref:`owning-entity <d_24a2377ce1bd600697c2d2dd58624524>` |  |  | 

.. _d_24a2377ce1bd600697c2d2dd58624524:

owning-entity Model Structure
-----------------------------

describes an owning-entity
###### Related Nodes
- FROM service-instance( service-instance BelongsTo owning-entity, MANY2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        owning-entity-id | Yes | string |  |  | UUID of an owning entity
        owning-entity-name | Yes | string |  |  | Owning entity name
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_c576aa1c4238465fa72af06f96b4feef:

platform Model Structure
------------------------

describes a platform
###### Related Nodes
- TO generic-vnf( platform Uses generic-vnf, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        platform-name | Yes | string |  |  | Name of the platform
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_0ffb5651bff9e2827913c502ef1105ff:

platforms Model Structure
-------------------------

Collection of platforms


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        platform | No | array of :ref:`platform <d_c576aa1c4238465fa72af06f96b4feef>` |  |  | 

.. _d_e9de9cd8a8cd53a8e8ccbab0a89a3bf0:

project Model Structure
-----------------------

describes the project
###### Related Nodes
- TO service-instance( project Uses service-instance, ONE2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        project-name | Yes | string |  |  | Name of the project deploying a service
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_2c4eaf0d859776872b364ba279823a75:

projects Model Structure
------------------------

Collection of projects


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        project | No | array of :ref:`project <d_e9de9cd8a8cd53a8e8ccbab0a89a3bf0>` |  |  | 

.. _d_2e28897978d0dd1b59eef0f2d7124064:

related-to-property Model Structure
-----------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        property-key | No | string |  |  | Key part of a key/value pair
        property-value | No | string |  |  | Value part of a key/value pair

.. _d_3a86fb483f7ec2bab2dad50fd3a6d612:

relationship Model Structure
----------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        related-link | No | string |  |  | URL to the object in A&AI.
        related-to | No | string |  |  | A keyword provided by A&AI to indicate type of node.
        related-to-property | No | array of :ref:`related-to-property <d_2e28897978d0dd1b59eef0f2d7124064>` |  |  | 
        relationship-data | No | array of :ref:`relationship-data <d_4f9097a364db6ef400e8e74489f41157>` |  |  | 
        relationship-label | No | string |  |  | The edge label for this relationship.

.. _d_4f9097a364db6ef400e8e74489f41157:

relationship-data Model Structure
---------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-key | Yes | string |  |  | A keyword provided by A&AI to indicate an attribute.
        relationship-value | Yes | string |  |  | Value of the attribute.

.. _d_1a9c9e0d93ce8e9a2fdae2fae114a5e3:

relationship-list Model Structure
---------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 

.. _d_4dece517d5f6a71e0094e07bca14006b:

service-instance Model Structure
--------------------------------

Instance of a service
###### Related Nodes
- TO generic-vnf( service-instance ComposedOf generic-vnf, ONE2MANY, will delete target node)
- TO l3-network( service-instance ComposedOf l3-network, ONE2MANY, will delete target node)
- TO owning-entity( service-instance BelongsTo owning-entity, MANY2ONE, will delete target node)
- TO allotted-resource( service-instance Uses allotted-resource, MANY2MANY, will delete target node)
- TO configuration( service-instance Uses configuration, ONE2MANY, will delete target node)
- TO connector( service-instance Uses connector, MANY2MANY, will delete target node)
- TO ctag-assignment( service-instance Uses ctag-assignment, ONE2MANY, will delete target node)
- TO cvlan-tag( service-instance ComposedOf cvlan-tag, MANY2MANY, will delete target node)
- TO instance-group( service-instance MemberOf instance-group, MANY2MANY, will delete target node)
- TO logical-link( service-instance Uses logical-link, ONE2MANY, will delete target node)(2)
- TO pnf( service-instance ComposedOf pnf, ONE2MANY, will delete target node)
- TO service-instance( service-instance ComposedOf service-instance, ONE2MANY, will delete target node)
- TO vlan( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- TO zone( service-instance LocatedIn zone, MANY2ONE, will delete target node)
- TO service-subscription( service-instance BelongsTo service-subscription, MANY2ONE, will delete target node)(4)
- TO vce( service-instance ComposedOf vce, ONE2MANY, will delete target node)
- TO model-ver( service-instance IsA model-ver, Many2One, will delete target node)
- TO configuration( service-instance Uses configuration, MANY2MANY, will delete target node)
- TO vpn-binding( service-instance Uses vpn-binding, MANY2ONE, will delete target node)
- FROM project( project Uses service-instance, ONE2MANY, will delete target node)
- FROM allotted-resource( allotted-resource BelongsTo service-instance, MANY2ONE, will delete target node)(1)
- FROM metadatum( metadatum BelongsTo service-instance, MANY2ONE, will delete target node)(1)
- FROM forwarding-path( forwarding-path AppliesTo service-instance, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration BelongsTo service-instance, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf service-instance, ONE2MANY, will delete target node)

-(1) IF this SERVICE-INSTANCE node is deleted, this FROM node is DELETED also
-(2) IF this SERVICE-INSTANCE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this SERVICE-INSTANCE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        allotted-resources | No | array of :ref:`allotted-resource <d_74e3067c98649f9bd125a1f12efff94c>` |  |  | 
        bandwidth-down-wan1 | No | string |  |  | indicates the downstream bandwidth this service will use on the WAN1 port of the physical device.
        bandwidth-down-wan2 | No | string |  |  | indicates the downstream bandwidth this service will use on the WAN2 port of the physical device.
        bandwidth-total | No | string |  |  | Indicates the total bandwidth to be used for this service.
        bandwidth-up-wan1 | No | string |  |  | indicates the upstream bandwidth this service will use on the WAN1 port of the physical device.
        bandwidth-up-wan2 | No | string |  |  | indicates the upstream bandwidth this service will use on the WAN2 port of the physical device.
        environment-context | No | string |  |  | This field will store the environment context assigned to the service-instance.
        metadata | No | array of :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>` |  |  | 
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        orchestration-status | No | string |  |  | Orchestration status of this service.
        persona-model-version | No | string |  |  | the ASDC model version for this resource or service model.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        selflink | No | string |  |  | Path to the controller object.
        service-instance-id | Yes | string |  |  | Uniquely identifies this instance of a service
        service-instance-location-id | No | string |  |  | An identifier that customers assign to the location where this service is being used.
        service-instance-name | No | string |  |  | This field will store a name assigned to the service-instance.
        service-role | No | string |  |  | String capturing the service role.
        service-type | No | string |  |  | String capturing type of service.
        vhn-portal-url | No | string |  |  | URL customers will use to access the vHN Portal.
        widget-model-id | No | string |  |  | the ASDC data dictionary widget model. This maps directly to the A&AI widget.
        widget-model-version | No | string |  |  | the ASDC data dictionary version of the widget model.This maps directly to the A&AI version of the widget.
        workload-context | No | string |  |  | This field will store the workload context assigned to the service-instance.

.. _d_6f012b6817169c6f77c16b1bf155d09c:

service-instances Model Structure
---------------------------------

Collection of service instances


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        service-instance | No | array of :ref:`service-instance <d_4dece517d5f6a71e0094e07bca14006b>` |  |  | 

.. _d_11c1b150c0c2ac297721ad7dedd11ff0:

service-subscription Model Structure
------------------------------------

Object that group service instances.
###### Related Nodes
- TO customer( service-subscription BelongsTo customer, MANY2ONE, will delete target node)(4)
- TO tenant( service-subscription Uses tenant, MANY2MANY, will delete target node)
- FROM service-instance( service-instance BelongsTo service-subscription, MANY2ONE, will delete target node)(1)

-(1) IF this SERVICE-SUBSCRIPTION node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this SERVICE-SUBSCRIPTION is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        service-instances | No | array of :ref:`service-instance <d_4dece517d5f6a71e0094e07bca14006b>` |  |  | 
        service-type | Yes | string |  |  | Value defined by orchestration to identify this service across ECOMP.
        temp-ub-sub-account-id | No | string |  |  | This property will be deleted from A&AI in the near future. Only stop gap solution.

.. _d_f60fad7ffa7385f016efae73cd634c31:

service-subscriptions Model Structure
-------------------------------------

Collection of objects that group service instances.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        service-subscription | No | array of :ref:`service-subscription <d_11c1b150c0c2ac297721ad7dedd11ff0>` |  |  | 

.. _d_85f7215a4cda082db6705b64e5dbf328:

tunnel-xconnect Model Structure
-------------------------------

Represents the specifics of a tunnel cross connect piece of a resource that gets separately allotted
###### Related Nodes
- TO allotted-resource( tunnel-xconnect BelongsTo allotted-resource, ONE2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this TUNNEL-XCONNECT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        bandwidth-down-wan1 | No | string |  |  | The WAN downlink bandwidth for WAN1
        bandwidth-down-wan2 | No | string |  |  | The WAN downlink bandwidth for WAN2
        bandwidth-up-wan1 | No | string |  |  | The WAN uplink bandwidth for WAN1
        bandwidth-up-wan2 | No | string |  |  | The WAN uplink bandwidth for WAN2
        id | Yes | string |  |  | Allotted Resource id UUID assigned to this instance.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Concurrency value

.. _d_826c51b048c1d852486a2752adea1274:

tunnel-xconnects Model Structure
--------------------------------

This object is used to store the specific tunnel cross connect aspects of an allotted resource


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        tunnel-xconnect | No | array of :ref:`tunnel-xconnect <d_85f7215a4cda082db6705b64e5dbf328>` |  |  | 

