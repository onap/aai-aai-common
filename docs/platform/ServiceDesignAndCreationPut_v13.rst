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

SERVICEDESIGNANDCREATION
~~~~~~~~~~~~~~~~~~~~~~~~




PUT ``/service-design-and-creation/models/model/{model-invariant-id}/relationship-list/relationship``
-----------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing model

Description
+++++++++++

.. raw:: html

    Create or update an existing model.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC


Request
+++++++



.. _d_41564b5f107858de1fa5b269417622a8:

Body
^^^^

Subgraph definition provided by ASDC to describe an inventory asset and its connections related to ASDC models, independent of version
###### Related Nodes
- TO model( model-ver BelongsTo model, MANY2ONE, will delete target node)(1)
- FROM instance-group( instance-group Targets model, MANY2MANY, will delete target node)
- FROM model-ver( model-ver BelongsTo model, MANY2ONE, will delete target node)(1)
- FROM named-query( named-query AppliesTo model, ONE2MANY, will delete target node)
- FROM named-query-element( named-query-element IsA model, MANY2ONE, will delete target node)

-(1) IF this MODEL node is deleted, this FROM node is DELETED also
-MODEL cannot be deleted if related to INSTANCE-GROUP,NAMED-QUERY,NAMED-QUERY-ELEMENT


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        model-invariant-id | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-type | Yes | string |  |  | Type of the model, e.g., service, resource, widget, etc.
        model-vers | No | array of :ref:`model-ver <d_44e2e2051beba3b3b1b1048d4c2c0a55>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. code-block:: javascript

    {
        "model-invariant-id": "somestring",
        "model-type": "somestring",
        "model-vers": [
            {
                "distribution-status": "somestring",
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
                "model-description": "somestring",
                "model-elements": [
                    {
                        "cardinality": "somestring",
                        "linkage-points": "somestring",
                        "model-constraints": [
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-element-uuid": "somestring",
                        "model-elements": [
                            {},
                            {}
                        ],
                        "new-data-del-flag": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "cardinality": "somestring",
                        "linkage-points": "somestring",
                        "model-constraints": [
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-element-uuid": "somestring",
                        "model-elements": [
                            {},
                            {}
                        ],
                        "new-data-del-flag": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                "model-name": "somestring",
                "model-version": "somestring",
                "model-version-id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "distribution-status": "somestring",
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
                "model-description": "somestring",
                "model-elements": [
                    {
                        "cardinality": "somestring",
                        "linkage-points": "somestring",
                        "model-constraints": [
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-element-uuid": "somestring",
                        "model-elements": [
                            {},
                            {}
                        ],
                        "new-data-del-flag": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "cardinality": "somestring",
                        "linkage-points": "somestring",
                        "model-constraints": [
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            },
                            {
                                "constrained-element-set-uuid-to-replace": "somestring",
                                "constrained-element-sets": [
                                    {
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "check-type": "somestring",
                                        "constrained-element-set-uuid": "somestring",
                                        "constraint-type": "somestring",
                                        "element-choice-sets": [
                                            {
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                                "cardinality": "somestring",
                                                "element-choice-set-name": "somestring",
                                                "element-choice-set-uuid": "somestring",
                                                "model-elements": [],
                                                "relationship-list": [
                                                    {
                                                        "related-link": "somestring",
                                                        "related-to": "somestring",
                                                        "relationship-data": [
                                                            {
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
                                                        "relationship-data": [
                                                            {
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
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "model-constraint-uuid": "somestring",
                                "resource-version": "somestring"
                            }
                        ],
                        "model-element-uuid": "somestring",
                        "model-elements": [
                            {},
                            {}
                        ],
                        "new-data-del-flag": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                "model-name": "somestring",
                "model-version": "somestring",
                "model-version-id": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/relationship-list/relationship``
---------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing model-ver

Description
+++++++++++

.. raw:: html

    Create or update an existing model-ver.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC


Request
+++++++



.. _d_44e2e2051beba3b3b1b1048d4c2c0a55:

Body
^^^^

Subgraph definition provided by ASDC to describe a specific version of an inventory asset and its connections related to ASDC models
###### Related Nodes
- TO model( model-ver BelongsTo model, MANY2ONE, will delete target node)(4)
- FROM model-element( model-element IsA model-ver, MANY2ONE, will delete target node)
- FROM metadatum( metadatum BelongsTo model-ver, MANY2ONE, will delete target node)(1)
- FROM model-element( model-element BelongsTo model-ver, MANY2ONE, will delete target node)(1)
- FROM allotted-resource( allotted-resource IsA model-ver, Many2One, will delete target node)
- FROM generic-vnf( generic-vnf IsA model-ver, Many2One, will delete target node)
- FROM l3-network( l3-network IsA model-ver, Many2One, will delete target node)
- FROM logical-link( logical-link IsA model-ver, Many2One, will delete target node)
- FROM service-instance( service-instance IsA model-ver, Many2One, will delete target node)
- FROM vf-module( vf-module IsA model-ver, Many2One, will delete target node)

-(1) IF this MODEL-VER node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this MODEL-VER is DELETED also
-MODEL-VER cannot be deleted if related to MODEL-ELEMENT


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        distribution-status | No | string |  |  | Distribution Status
        metadata | No | array of :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>` |  |  | 
        model-description | No | string |  |  | Description
        model-elements | No | array of :ref:`model-element <d_a6d9d6768611b7241c4c3ba0ccbad485>` |  |  | 
        model-name | Yes | string |  |  | Name of the model, which can change from version to version.
        model-version | Yes | string |  |  | Version
        model-version-id | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. code-block:: javascript

    {
        "distribution-status": "somestring",
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
        "model-description": "somestring",
        "model-elements": [
            {
                "cardinality": "somestring",
                "linkage-points": "somestring",
                "model-constraints": [
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-element-uuid": "somestring",
                "model-elements": [
                    {},
                    {}
                ],
                "new-data-del-flag": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "cardinality": "somestring",
                "linkage-points": "somestring",
                "model-constraints": [
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                        "cardinality": "somestring",
                                        "element-choice-set-name": "somestring",
                                        "element-choice-set-uuid": "somestring",
                                        "model-elements": [],
                                        "relationship-list": [
                                            {
                                                "related-link": "somestring",
                                                "related-to": "somestring",
                                                "relationship-data": [
                                                    {
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
                                                "relationship-data": [
                                                    {
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
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-element-uuid": "somestring",
                "model-elements": [
                    {},
                    {}
                ],
                "new-data-del-flag": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
        "model-name": "somestring",
        "model-version": "somestring",
        "model-version-id": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/model-elements/model-element/{model-element-uuid}/relationship-list/relationship``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing metadatum

Description
+++++++++++

.. raw:: html

    Create or update an existing metadatum.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        metaname | path | Yes | string |  |  | 


Request
+++++++



.. _d_86c5a7078292838659223f545f7cca0a:

Body
^^^^

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

.. code-block:: javascript

    {
        "metaname": "somestring",
        "metaval": "somestring",
        "resource-version": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/model-elements/model-element/{model-element-uuid}/model-constraints/model-constraint/{model-constraint-uuid}/constrained-element-sets/constrained-element-set/{constrained-element-set-uuid}/relationship-list/relationship``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing model-constraint

Description
+++++++++++

.. raw:: html

    Create or update an existing model-constraint.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        model-element-uuid | path | Yes | string |  |  | 
        model-constraint-uuid | path | Yes | string |  |  | 


Request
+++++++



.. _d_68604fe833ceb6d667412d26e6ad1eef:

Body
^^^^

This is how we would capture constraints defining allowed sets of elements.
###### Related Nodes
- TO model-element( model-constraint BelongsTo model-element, MANY2ONE, will delete target node)(4)
- FROM constrained-element-set( constrained-element-set BelongsTo model-constraint, MANY2ONE, will delete target node)(1)

-(1) IF this MODEL-CONSTRAINT node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this MODEL-CONSTRAINT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        constrained-element-set-uuid-to-replace | Yes | string |  |  | 
        constrained-element-sets | No | array of :ref:`constrained-element-set <d_8c4d3c8764dcf6da35d5677bc057567c>` |  |  | 
        model-constraint-uuid | Yes | string |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. code-block:: javascript

    {
        "constrained-element-set-uuid-to-replace": "somestring",
        "constrained-element-sets": [
            {
                "check-type": "somestring",
                "constrained-element-set-uuid": "somestring",
                "constraint-type": "somestring",
                "element-choice-sets": [
                    {
                        "cardinality": "somestring",
                        "element-choice-set-name": "somestring",
                        "element-choice-set-uuid": "somestring",
                        "model-elements": [
                            {
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "cardinality": "somestring",
                        "element-choice-set-name": "somestring",
                        "element-choice-set-uuid": "somestring",
                        "model-elements": [
                            {
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "check-type": "somestring",
                "constrained-element-set-uuid": "somestring",
                "constraint-type": "somestring",
                "element-choice-sets": [
                    {
                        "cardinality": "somestring",
                        "element-choice-set-name": "somestring",
                        "element-choice-set-uuid": "somestring",
                        "model-elements": [
                            {
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "cardinality": "somestring",
                        "element-choice-set-name": "somestring",
                        "element-choice-set-uuid": "somestring",
                        "model-elements": [
                            {
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "cardinality": "somestring",
                                "linkage-points": "somestring",
                                "model-constraints": [
                                    {},
                                    {}
                                ],
                                "model-element-uuid": "somestring",
                                "model-elements": [
                                    {},
                                    {}
                                ],
                                "new-data-del-flag": "somestring",
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
        "model-constraint-uuid": "somestring",
        "resource-version": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/model-elements/model-element/{model-element-uuid}/model-constraints/model-constraint/{model-constraint-uuid}/constrained-element-sets/constrained-element-set/{constrained-element-set-uuid}/element-choice-sets/element-choice-set/{element-choice-set-uuid}/relationship-list/relationship``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing element-choice-set

Description
+++++++++++

.. raw:: html

    Create or update an existing element-choice-set.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        model-element-uuid | path | Yes | string |  |  | 
        model-constraint-uuid | path | Yes | string |  |  | 
        constrained-element-set-uuid | path | Yes | string |  |  | 
        element-choice-set-uuid | path | Yes | string |  |  | 


Request
+++++++



.. _d_0b5183c0cde1e93de0c56a3cc798ad63:

Body
^^^^

This is how we would capture constraints defining allowed sets of elements.
###### Related Nodes
- TO constrained-element-set( element-choice-set BelongsTo constrained-element-set, MANY2ONE, will delete target node)(4)
- FROM model-element( model-element BelongsTo element-choice-set, MANY2ONE, will delete target node)(1)

-(1) IF this ELEMENT-CHOICE-SET node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this ELEMENT-CHOICE-SET is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cardinality | No | string |  |  | 
        element-choice-set-name | Yes | string |  |  | 
        element-choice-set-uuid | Yes | string |  |  | 
        model-elements | No | array of :ref:`model-element <d_a6d9d6768611b7241c4c3ba0ccbad485>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | 

.. code-block:: javascript

    {
        "cardinality": "somestring",
        "element-choice-set-name": "somestring",
        "element-choice-set-uuid": "somestring",
        "model-elements": [
            {
                "cardinality": "somestring",
                "linkage-points": "somestring",
                "model-constraints": [
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-element-uuid": "somestring",
                "model-elements": [
                    {},
                    {}
                ],
                "new-data-del-flag": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "cardinality": "somestring",
                "linkage-points": "somestring",
                "model-constraints": [
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "constrained-element-set-uuid-to-replace": "somestring",
                        "constrained-element-sets": [
                            {
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                                "check-type": "somestring",
                                "constrained-element-set-uuid": "somestring",
                                "constraint-type": "somestring",
                                "element-choice-sets": [
                                    {},
                                    {}
                                ],
                                "relationship-list": [
                                    {
                                        "related-link": "somestring",
                                        "related-to": "somestring",
                                        "relationship-data": [
                                            {
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
                                        "relationship-data": [
                                            {
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
                        "model-constraint-uuid": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "model-element-uuid": "somestring",
                "model-elements": [
                    {},
                    {}
                ],
                "new-data-del-flag": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/named-queries/named-query/{named-query-uuid}/relationship-list/relationship``
----------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing named-query

Description
+++++++++++

.. raw:: html

    Create or update an existing named-query.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        named-query-uuid | path | Yes | string |  |  | 


Request
+++++++



.. _d_943db4dd4041a416b4822f8eba29d48a:

Body
^^^^

TBD
###### Related Nodes
- TO named-query( named-query-element BelongsTo named-query, ONE2ONE, will delete target node)(1)
- TO model( named-query AppliesTo model, ONE2MANY, will delete target node)
- FROM named-query-element( named-query-element BelongsTo named-query, ONE2ONE, will delete target node)(1)

-(1) IF this NAMED-QUERY node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        description | No | string |  |  | 
        named-query-elements | No | array of :ref:`named-query-element <d_2fa1d7ab0e4e4100f84b81737c9e808b>` |  |  | 
        named-query-name | Yes | string |  |  | 
        named-query-uuid | Yes | string |  |  | 
        named-query-version | Yes | string |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        required-input-param | No | string |  |  | 
        resource-version | No | string |  |  | 

.. code-block:: javascript

    {
        "description": "somestring",
        "named-query-elements": [
            {
                "do-not-output": "somestring",
                "named-query-element-uuid": "somestring",
                "named-query-elements": [],
                "property-collect-list": "somestring",
                "property-constraints": [
                    {
                        "constraint-type": "somestring",
                        "property-constraint-uuid": "somestring",
                        "property-name": "somestring",
                        "property-value": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "constraint-type": "somestring",
                        "property-constraint-uuid": "somestring",
                        "property-name": "somestring",
                        "property-value": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "property-limit-desc": "somestring",
                "related-lookups": [
                    {
                        "property-collect-list": "somestring",
                        "related-lookup-uuid": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "source-node-property": "somestring",
                        "source-node-type": "somestring",
                        "target-node-property": "somestring",
                        "target-node-type": "somestring"
                    },
                    {
                        "property-collect-list": "somestring",
                        "related-lookup-uuid": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "source-node-property": "somestring",
                        "source-node-type": "somestring",
                        "target-node-property": "somestring",
                        "target-node-type": "somestring"
                    }
                ],
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "do-not-output": "somestring",
                "named-query-element-uuid": "somestring",
                "named-query-elements": [],
                "property-collect-list": "somestring",
                "property-constraints": [
                    {
                        "constraint-type": "somestring",
                        "property-constraint-uuid": "somestring",
                        "property-name": "somestring",
                        "property-value": "somestring",
                        "resource-version": "somestring"
                    },
                    {
                        "constraint-type": "somestring",
                        "property-constraint-uuid": "somestring",
                        "property-name": "somestring",
                        "property-value": "somestring",
                        "resource-version": "somestring"
                    }
                ],
                "property-limit-desc": "somestring",
                "related-lookups": [
                    {
                        "property-collect-list": "somestring",
                        "related-lookup-uuid": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "source-node-property": "somestring",
                        "source-node-type": "somestring",
                        "target-node-property": "somestring",
                        "target-node-type": "somestring"
                    },
                    {
                        "property-collect-list": "somestring",
                        "related-lookup-uuid": "somestring",
                        "relationship-list": [
                            {
                                "related-link": "somestring",
                                "related-to": "somestring",
                                "relationship-data": [
                                    {
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
                                "relationship-data": [
                                    {
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
                        "source-node-property": "somestring",
                        "source-node-type": "somestring",
                        "target-node-property": "somestring",
                        "target-node-type": "somestring"
                    }
                ],
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
        "named-query-name": "somestring",
        "named-query-uuid": "somestring",
        "named-query-version": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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
        "required-input-param": "somestring",
        "resource-version": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/named-queries/named-query/{named-query-uuid}/named-query-elements/named-query-element/{named-query-element-uuid}/relationship-list/relationship``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing named-query-element

Description
+++++++++++

.. raw:: html

    Create or update an existing named-query-element.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        named-query-uuid | path | Yes | string |  |  | 
        named-query-element-uuid | path | Yes | string |  |  | 


Request
+++++++



.. _d_2fa1d7ab0e4e4100f84b81737c9e808b:

Body
^^^^

TBD
###### Related Nodes
- TO named-query( named-query-element BelongsTo named-query, ONE2ONE, will delete target node)(4)
- TO model( named-query-element IsA model, MANY2ONE, will delete target node)
- TO named-query-element( named-query-element BelongsTo named-query-element, MANY2ONE, will delete target node)(4)
- FROM property-constraint( property-constraint BelongsTo named-query-element, MANY2ONE, will delete target node)(1)
- FROM related-lookup( related-lookup BelongsTo named-query-element, MANY2ONE, will delete target node)(1)
- FROM named-query-element( named-query-element BelongsTo named-query-element, MANY2ONE, will delete target node)(4)

-(1) IF this NAMED-QUERY-ELEMENT node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this NAMED-QUERY-ELEMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        do-not-output | No | string |  |  | 
        named-query-element-uuid | Yes | string |  |  | 
        named-query-elements | No | array of :ref:`named-query-element <d_2fa1d7ab0e4e4100f84b81737c9e808b>` |  |  | 
        property-collect-list | No | string |  |  | 
        property-constraints | No | array of :ref:`property-constraint <d_e84c883e978864d3b68e65653c61c285>` |  |  | 
        property-limit-desc | No | string |  |  | 
        related-lookups | No | array of :ref:`related-lookup <d_62bdd500274d40804b15329ae1d47c6f>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | 

.. code-block:: javascript

    {
        "do-not-output": "somestring",
        "named-query-element-uuid": "somestring",
        "named-query-elements": [
            {},
            {}
        ],
        "property-collect-list": "somestring",
        "property-constraints": [
            {
                "constraint-type": "somestring",
                "property-constraint-uuid": "somestring",
                "property-name": "somestring",
                "property-value": "somestring",
                "resource-version": "somestring"
            },
            {
                "constraint-type": "somestring",
                "property-constraint-uuid": "somestring",
                "property-name": "somestring",
                "property-value": "somestring",
                "resource-version": "somestring"
            }
        ],
        "property-limit-desc": "somestring",
        "related-lookups": [
            {
                "property-collect-list": "somestring",
                "related-lookup-uuid": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "source-node-property": "somestring",
                "source-node-type": "somestring",
                "target-node-property": "somestring",
                "target-node-type": "somestring"
            },
            {
                "property-collect-list": "somestring",
                "related-lookup-uuid": "somestring",
                "relationship-list": [
                    {
                        "related-link": "somestring",
                        "related-to": "somestring",
                        "relationship-data": [
                            {
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
                        "relationship-data": [
                            {
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
                "source-node-property": "somestring",
                "source-node-type": "somestring",
                "target-node-property": "somestring",
                "target-node-type": "somestring"
            }
        ],
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/named-queries/named-query/{named-query-uuid}/named-query-elements/named-query-element/{named-query-element-uuid}/related-lookups/related-lookup/{related-lookup-uuid}/relationship-list/relationship``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing property-constraint

Description
+++++++++++

.. raw:: html

    Create or update an existing property-constraint.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        named-query-uuid | path | Yes | string |  |  | 
        named-query-element-uuid | path | Yes | string |  |  | 
        property-constraint-uuid | path | Yes | string |  |  | 


Request
+++++++



.. _d_e84c883e978864d3b68e65653c61c285:

Body
^^^^

TBD
###### Related Nodes
- TO named-query-element( property-constraint BelongsTo named-query-element, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this PROPERTY-CONSTRAINT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        constraint-type | Yes | string |  |  | 
        property-constraint-uuid | Yes | string |  |  | 
        property-name | Yes | string |  |  | 
        property-value | Yes | string |  |  | 
        resource-version | No | string |  |  | 

.. code-block:: javascript

    {
        "constraint-type": "somestring",
        "property-constraint-uuid": "somestring",
        "property-name": "somestring",
        "property-value": "somestring",
        "resource-version": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/service-capabilities/service-capability/{service-type}/{vnf-type}/relationship-list/relationship``
-------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing service-capability

Description
+++++++++++

.. raw:: html

    Create or update an existing service-capability.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        service-type | path | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        vnf-type | path | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.


Request
+++++++



.. _d_e38fa86701414740c470e6eb2ce2ecae:

Body
^^^^

Early definition of server/resource pairings, likely to be replaced by models.  No new use should be made of this.
###### Related Nodes
- FROM availability-zone( availability-zone AppliesTo service-capability, MANY2MANY, will delete target node)
- FROM oam-network( oam-network AppliesTo service-capability, MANY2MANY, will delete target node)

-SERVICE-CAPABILITY cannot be deleted if related to AVAILABILITY-ZONE,OAM-NETWORK


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        service-type | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.

.. code-block:: javascript

    {
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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
        "service-type": "somestring",
        "vnf-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/services/service/{service-id}/relationship-list/relationship``
-------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing service

Description
+++++++++++

.. raw:: html

    Create or update an existing service.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        service-id | path | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.


Request
+++++++



.. _d_d3b85fa90604443b1743600895bf6c5f:

Body
^^^^

Stand-in for service model definitions.  Likely to be deprecated in favor of models from ASDC.  Does not strictly map to ASDC services.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        service-description | Yes | string |  |  | Description of the service
        service-id | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        service-selflink | No | string |  |  | URL to endpoint where AAI can get more details
        service-version | No | string |  |  | service version

.. code-block:: javascript

    {
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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
        "service-description": "somestring",
        "service-id": "somestring",
        "service-selflink": "somestring",
        "service-version": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PUT ``/service-design-and-creation/vnf-images/vnf-image/{vnf-image-uuid}/relationship-list/relationship``
---------------------------------------------------------------------------------------------------------


Summary
+++++++

create or update an existing vnf-image

Description
+++++++++++

.. raw:: html

    Create or update an existing vnf-image.
#
Note! This PUT method has a corresponding PATCH method that can be used to update just a few of the fields of an existing object, rather than a full object replacement.  An example can be found in the [PATCH section] below


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-image-uuid | path | Yes | string |  |  | Unique ID of this asset


Request
+++++++



.. _d_d2cbb2a0fa7d7e055e8c2c36caf49bdc:

Body
^^^^

Image object that pertain to a VNF that doesn't have associated vservers.  This is a kludge.
###### Related Nodes
- FROM generic-vnf( generic-vnf Uses vnf-image, MANY2ONE, will delete target node)

-VNF-IMAGE cannot be deleted if related to GENERIC-VNF


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        application | Yes | string |  |  | The application that the image instantiates.
        application-vendor | Yes | string |  |  | The vendor of the application.
        application-version | No | string |  |  | The version of the application.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        vnf-image-uuid | Yes | string |  |  | Unique ID of this asset

.. code-block:: javascript

    {
        "application": "somestring",
        "application-vendor": "somestring",
        "application-version": "somestring",
        "relationship-list": [
            {
                "related-link": "somestring",
                "related-to": "somestring",
                "relationship-data": [
                    {
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
                "relationship-data": [
                    {
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
        "vnf-image-uuid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/models/model/{model-invariant-id}/relationship-list/relationship``
--------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing model

Description
+++++++++++

.. raw:: html

    delete an existing model

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/relationship-list/relationship``
------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing model-ver

Description
+++++++++++

.. raw:: html

    delete an existing model-ver

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/model-elements/model-element/{model-element-uuid}/relationship-list/relationship``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing metadatum

Description
+++++++++++

.. raw:: html

    delete an existing metadatum

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        metaname | path | Yes | string |  |  | 
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/model-elements/model-element/{model-element-uuid}/model-constraints/model-constraint/{model-constraint-uuid}/constrained-element-sets/constrained-element-set/{constrained-element-set-uuid}/relationship-list/relationship``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing model-constraint

Description
+++++++++++

.. raw:: html

    delete an existing model-constraint

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        model-element-uuid | path | Yes | string |  |  | 
        model-constraint-uuid | path | Yes | string |  |  | 
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/models/model/{model-invariant-id}/model-vers/model-ver/{model-version-id}/model-elements/model-element/{model-element-uuid}/model-constraints/model-constraint/{model-constraint-uuid}/constrained-element-sets/constrained-element-set/{constrained-element-set-uuid}/element-choice-sets/element-choice-set/{element-choice-set-uuid}/relationship-list/relationship``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing element-choice-set

Description
+++++++++++

.. raw:: html

    delete an existing element-choice-set

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        model-invariant-id | path | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-version-id | path | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        model-element-uuid | path | Yes | string |  |  | 
        model-constraint-uuid | path | Yes | string |  |  | 
        constrained-element-set-uuid | path | Yes | string |  |  | 
        element-choice-set-uuid | path | Yes | string |  |  | 
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/named-queries/named-query/{named-query-uuid}/relationship-list/relationship``
-------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing named-query

Description
+++++++++++

.. raw:: html

    delete an existing named-query

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        named-query-uuid | path | Yes | string |  |  | 
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/named-queries/named-query/{named-query-uuid}/named-query-elements/named-query-element/{named-query-element-uuid}/relationship-list/relationship``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing named-query-element

Description
+++++++++++

.. raw:: html

    delete an existing named-query-element

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        named-query-uuid | path | Yes | string |  |  | 
        named-query-element-uuid | path | Yes | string |  |  | 
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/named-queries/named-query/{named-query-uuid}/named-query-elements/named-query-element/{named-query-element-uuid}/related-lookups/related-lookup/{related-lookup-uuid}/relationship-list/relationship``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing property-constraint

Description
+++++++++++

.. raw:: html

    delete an existing property-constraint

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        named-query-uuid | path | Yes | string |  |  | 
        named-query-element-uuid | path | Yes | string |  |  | 
        property-constraint-uuid | path | Yes | string |  |  | 
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/service-capabilities/service-capability/{service-type}/{vnf-type}/relationship-list/relationship``
----------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing service-capability

Description
+++++++++++

.. raw:: html

    delete an existing service-capability

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        service-type | path | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        vnf-type | path | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/services/service/{service-id}/relationship-list/relationship``
----------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing service

Description
+++++++++++

.. raw:: html

    delete an existing service

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        service-id | path | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






DELETE ``/service-design-and-creation/vnf-images/vnf-image/{vnf-image-uuid}/relationship-list/relationship``
------------------------------------------------------------------------------------------------------------


Summary
+++++++

delete an existing vnf-image

Description
+++++++++++

.. raw:: html

    delete an existing vnf-image

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-image-uuid | path | Yes | string |  |  | Unique ID of this asset
        resource-version | query | Yes | string |  |  | resource-version for concurrency


Request
+++++++


Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).




  
Data Structures
~~~~~~~~~~~~~~~

.. _d_8c4d3c8764dcf6da35d5677bc057567c:

constrained-element-set Model Structure
---------------------------------------

This is how we would capture constraints defining allowed sets of elements.
###### Related Nodes
- TO model-constraint( constrained-element-set BelongsTo model-constraint, MANY2ONE, will delete target node)(4)
- TO model-element( constrained-element-set BelongsTo model-element, MANY2ONE, will delete target node)(4)
- FROM element-choice-set( element-choice-set BelongsTo constrained-element-set, MANY2ONE, will delete target node)(1)

-(1) IF this CONSTRAINED-ELEMENT-SET node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this CONSTRAINED-ELEMENT-SET is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        check-type | Yes | string |  |  | 
        constrained-element-set-uuid | Yes | string |  |  | 
        constraint-type | Yes | string |  |  | 
        element-choice-sets | No | array of :ref:`element-choice-set <d_0b5183c0cde1e93de0c56a3cc798ad63>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | 

.. _d_38bc267d841191f24d53d79fa9b21d0a:

constrained-element-sets Model Structure
----------------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        constrained-element-set | No | array of :ref:`constrained-element-set <d_8c4d3c8764dcf6da35d5677bc057567c>` |  |  | 

.. _d_0b5183c0cde1e93de0c56a3cc798ad63:

element-choice-set Model Structure
----------------------------------

This is how we would capture constraints defining allowed sets of elements.
###### Related Nodes
- TO constrained-element-set( element-choice-set BelongsTo constrained-element-set, MANY2ONE, will delete target node)(4)
- FROM model-element( model-element BelongsTo element-choice-set, MANY2ONE, will delete target node)(1)

-(1) IF this ELEMENT-CHOICE-SET node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this ELEMENT-CHOICE-SET is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cardinality | No | string |  |  | 
        element-choice-set-name | Yes | string |  |  | 
        element-choice-set-uuid | Yes | string |  |  | 
        model-elements | No | array of :ref:`model-element <d_a6d9d6768611b7241c4c3ba0ccbad485>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | 

.. _d_6cbeaaa1ca4e02d8b6801a4e93c38645:

element-choice-sets Model Structure
-----------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        element-choice-set | No | array of :ref:`element-choice-set <d_0b5183c0cde1e93de0c56a3cc798ad63>` |  |  | 

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

.. _d_41564b5f107858de1fa5b269417622a8:

model Model Structure
---------------------

Subgraph definition provided by ASDC to describe an inventory asset and its connections related to ASDC models, independent of version
###### Related Nodes
- TO model( model-ver BelongsTo model, MANY2ONE, will delete target node)(1)
- FROM instance-group( instance-group Targets model, MANY2MANY, will delete target node)
- FROM model-ver( model-ver BelongsTo model, MANY2ONE, will delete target node)(1)
- FROM named-query( named-query AppliesTo model, ONE2MANY, will delete target node)
- FROM named-query-element( named-query-element IsA model, MANY2ONE, will delete target node)

-(1) IF this MODEL node is deleted, this FROM node is DELETED also
-MODEL cannot be deleted if related to INSTANCE-GROUP,NAMED-QUERY,NAMED-QUERY-ELEMENT


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        model-invariant-id | Yes | string |  |  | Unique identifier corresponding to the main definition of a model in ASDC
        model-type | Yes | string |  |  | Type of the model, e.g., service, resource, widget, etc.
        model-vers | No | array of :ref:`model-ver <d_44e2e2051beba3b3b1b1048d4c2c0a55>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_68604fe833ceb6d667412d26e6ad1eef:

model-constraint Model Structure
--------------------------------

This is how we would capture constraints defining allowed sets of elements.
###### Related Nodes
- TO model-element( model-constraint BelongsTo model-element, MANY2ONE, will delete target node)(4)
- FROM constrained-element-set( constrained-element-set BelongsTo model-constraint, MANY2ONE, will delete target node)(1)

-(1) IF this MODEL-CONSTRAINT node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this MODEL-CONSTRAINT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        constrained-element-set-uuid-to-replace | Yes | string |  |  | 
        constrained-element-sets | No | array of :ref:`constrained-element-set <d_8c4d3c8764dcf6da35d5677bc057567c>` |  |  | 
        model-constraint-uuid | Yes | string |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_f6d56af7a825053be6ac413ce9c40908:

model-constraints Model Structure
---------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        model-constraint | No | array of :ref:`model-constraint <d_68604fe833ceb6d667412d26e6ad1eef>` |  |  | 

.. _d_a6d9d6768611b7241c4c3ba0ccbad485:

model-element Model Structure
-----------------------------

Defines how other models combine to make up a higher-level model.
###### Related Nodes
- TO element-choice-set( model-element BelongsTo element-choice-set, MANY2ONE, will delete target node)(4)
- TO model-element( model-element BelongsTo model-element, MANY2ONE, will delete target node)(4)
- TO model-ver( model-element IsA model-ver, MANY2ONE, will delete target node)
- TO model-ver( model-element BelongsTo model-ver, MANY2ONE, will delete target node)(4)
- FROM constrained-element-set( constrained-element-set BelongsTo model-element, MANY2ONE, will delete target node)(1)
- FROM model-constraint( model-constraint BelongsTo model-element, MANY2ONE, will delete target node)(1)
- FROM model-element( model-element BelongsTo model-element, MANY2ONE, will delete target node)(4)

-(1) IF this MODEL-ELEMENT node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this MODEL-ELEMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cardinality | Yes | string |  |  | How many of this type of element are required/allowed
        linkage-points | No | string |  |  | 
        model-constraints | No | array of :ref:`model-constraint <d_68604fe833ceb6d667412d26e6ad1eef>` |  |  | Describes new constraints on this model element that are not part of that model's definition
        model-element-uuid | Yes | string |  |  | 
        model-elements | No | array of :ref:`model-element <d_a6d9d6768611b7241c4c3ba0ccbad485>` |  |  | Defines how other models combine to make up a higher-level model
        new-data-del-flag | Yes | string |  |  | Indicates whether this element was created as part of instantiation from this model
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_826d396af46d7af26a3ac9dd71975566:

model-elements Model Structure
------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        model-element | No | array of :ref:`model-element <d_a6d9d6768611b7241c4c3ba0ccbad485>` |  |  | 

.. _d_44e2e2051beba3b3b1b1048d4c2c0a55:

model-ver Model Structure
-------------------------

Subgraph definition provided by ASDC to describe a specific version of an inventory asset and its connections related to ASDC models
###### Related Nodes
- TO model( model-ver BelongsTo model, MANY2ONE, will delete target node)(4)
- FROM model-element( model-element IsA model-ver, MANY2ONE, will delete target node)
- FROM metadatum( metadatum BelongsTo model-ver, MANY2ONE, will delete target node)(1)
- FROM model-element( model-element BelongsTo model-ver, MANY2ONE, will delete target node)(1)
- FROM allotted-resource( allotted-resource IsA model-ver, Many2One, will delete target node)
- FROM generic-vnf( generic-vnf IsA model-ver, Many2One, will delete target node)
- FROM l3-network( l3-network IsA model-ver, Many2One, will delete target node)
- FROM logical-link( logical-link IsA model-ver, Many2One, will delete target node)
- FROM service-instance( service-instance IsA model-ver, Many2One, will delete target node)
- FROM vf-module( vf-module IsA model-ver, Many2One, will delete target node)

-(1) IF this MODEL-VER node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this MODEL-VER is DELETED also
-MODEL-VER cannot be deleted if related to MODEL-ELEMENT


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        distribution-status | No | string |  |  | Distribution Status
        metadata | No | array of :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>` |  |  | 
        model-description | No | string |  |  | Description
        model-elements | No | array of :ref:`model-element <d_a6d9d6768611b7241c4c3ba0ccbad485>` |  |  | 
        model-name | Yes | string |  |  | Name of the model, which can change from version to version.
        model-version | Yes | string |  |  | Version
        model-version-id | Yes | string |  |  | Unique identifier corresponding to one version of a model in ASDC
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.

.. _d_eeabad2f3b0f9a650601512a8d313db7:

model-vers Model Structure
--------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        model-ver | No | array of :ref:`model-ver <d_44e2e2051beba3b3b1b1048d4c2c0a55>` |  |  | 

.. _d_4ed209a8deda3830072ae2bb4497f99d:

models Model Structure
----------------------

Collection of subgraph definitions provided by ASDC to describe the inventory assets and their connections related to ASDC models


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        model | No | array of :ref:`model <d_41564b5f107858de1fa5b269417622a8>` |  |  | 

.. _d_1f556297d0b349c4d9852edd2261bf63:

named-queries Model Structure
-----------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        named-query | No | array of :ref:`named-query <d_943db4dd4041a416b4822f8eba29d48a>` |  |  | 

.. _d_943db4dd4041a416b4822f8eba29d48a:

named-query Model Structure
---------------------------

TBD
###### Related Nodes
- TO named-query( named-query-element BelongsTo named-query, ONE2ONE, will delete target node)(1)
- TO model( named-query AppliesTo model, ONE2MANY, will delete target node)
- FROM named-query-element( named-query-element BelongsTo named-query, ONE2ONE, will delete target node)(1)

-(1) IF this NAMED-QUERY node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        description | No | string |  |  | 
        named-query-elements | No | array of :ref:`named-query-element <d_2fa1d7ab0e4e4100f84b81737c9e808b>` |  |  | 
        named-query-name | Yes | string |  |  | 
        named-query-uuid | Yes | string |  |  | 
        named-query-version | Yes | string |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        required-input-param | No | string |  |  | 
        resource-version | No | string |  |  | 

.. _d_2fa1d7ab0e4e4100f84b81737c9e808b:

named-query-element Model Structure
-----------------------------------

TBD
###### Related Nodes
- TO named-query( named-query-element BelongsTo named-query, ONE2ONE, will delete target node)(4)
- TO model( named-query-element IsA model, MANY2ONE, will delete target node)
- TO named-query-element( named-query-element BelongsTo named-query-element, MANY2ONE, will delete target node)(4)
- FROM property-constraint( property-constraint BelongsTo named-query-element, MANY2ONE, will delete target node)(1)
- FROM related-lookup( related-lookup BelongsTo named-query-element, MANY2ONE, will delete target node)(1)
- FROM named-query-element( named-query-element BelongsTo named-query-element, MANY2ONE, will delete target node)(4)

-(1) IF this NAMED-QUERY-ELEMENT node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this NAMED-QUERY-ELEMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        do-not-output | No | string |  |  | 
        named-query-element-uuid | Yes | string |  |  | 
        named-query-elements | No | array of :ref:`named-query-element <d_2fa1d7ab0e4e4100f84b81737c9e808b>` |  |  | 
        property-collect-list | No | string |  |  | 
        property-constraints | No | array of :ref:`property-constraint <d_e84c883e978864d3b68e65653c61c285>` |  |  | 
        property-limit-desc | No | string |  |  | 
        related-lookups | No | array of :ref:`related-lookup <d_62bdd500274d40804b15329ae1d47c6f>` |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | 

.. _d_30e94a50023bdf151fe3db42790c22f1:

named-query-elements Model Structure
------------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        named-query-element | No | array of :ref:`named-query-element <d_2fa1d7ab0e4e4100f84b81737c9e808b>` |  |  | 

.. _d_e84c883e978864d3b68e65653c61c285:

property-constraint Model Structure
-----------------------------------

TBD
###### Related Nodes
- TO named-query-element( property-constraint BelongsTo named-query-element, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this PROPERTY-CONSTRAINT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        constraint-type | Yes | string |  |  | 
        property-constraint-uuid | Yes | string |  |  | 
        property-name | Yes | string |  |  | 
        property-value | Yes | string |  |  | 
        resource-version | No | string |  |  | 

.. _d_bb54dfbe2ab6f787d7dbd52e50376074:

property-constraints Model Structure
------------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        property-constraint | No | array of :ref:`property-constraint <d_e84c883e978864d3b68e65653c61c285>` |  |  | 

.. _d_62bdd500274d40804b15329ae1d47c6f:

related-lookup Model Structure
------------------------------

TBD
###### Related Nodes
- TO named-query-element( related-lookup BelongsTo named-query-element, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this RELATED-LOOKUP is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        property-collect-list | No | string |  |  | 
        related-lookup-uuid | Yes | string |  |  | 
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | 
        source-node-property | Yes | string |  |  | 
        source-node-type | Yes | string |  |  | 
        target-node-property | Yes | string |  |  | 
        target-node-type | Yes | string |  |  | 

.. _d_2002da385e03db3a49131d2c1e6294be:

related-lookups Model Structure
-------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        related-lookup | No | array of :ref:`related-lookup <d_62bdd500274d40804b15329ae1d47c6f>` |  |  | 

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

.. _d_d3b85fa90604443b1743600895bf6c5f:

service Model Structure
-----------------------

Stand-in for service model definitions.  Likely to be deprecated in favor of models from ASDC.  Does not strictly map to ASDC services.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        service-description | Yes | string |  |  | Description of the service
        service-id | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        service-selflink | No | string |  |  | URL to endpoint where AAI can get more details
        service-version | No | string |  |  | service version

.. _d_403c006ad42d6d3d7a44df3be07fd1a6:

service-capabilities Model Structure
------------------------------------

Collection of service capabilities.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        service-capability | No | array of :ref:`service-capability <d_e38fa86701414740c470e6eb2ce2ecae>` |  |  | 

.. _d_e38fa86701414740c470e6eb2ce2ecae:

service-capability Model Structure
----------------------------------

Early definition of server/resource pairings, likely to be replaced by models.  No new use should be made of this.
###### Related Nodes
- FROM availability-zone( availability-zone AppliesTo service-capability, MANY2MANY, will delete target node)
- FROM oam-network( oam-network AppliesTo service-capability, MANY2MANY, will delete target node)

-SERVICE-CAPABILITY cannot be deleted if related to AVAILABILITY-ZONE,OAM-NETWORK


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        service-type | Yes | string |  |  | This gets defined by others to provide a unique ID for the service, we accept what is sent.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.

.. _d_b1fb609202c35474d300dda31ecfb64e:

service-design-and-creation Model Structure
-------------------------------------------

Namespace for objects managed by ASDC


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        models | No | array of :ref:`model <d_41564b5f107858de1fa5b269417622a8>` |  |  | 
        named-queries | No | array of :ref:`named-query <d_943db4dd4041a416b4822f8eba29d48a>` |  |  | 
        service-capabilities | No | array of :ref:`service-capability <d_e38fa86701414740c470e6eb2ce2ecae>` |  |  | 
        services | No | array of :ref:`service <d_d3b85fa90604443b1743600895bf6c5f>` |  |  | 
        vnf-images | No | array of :ref:`vnf-image <d_d2cbb2a0fa7d7e055e8c2c36caf49bdc>` |  |  | 

.. _d_68a060ec20fe259ca7f0e7d0d5f19758:

services Model Structure
------------------------

Collection of service model definitions.  Likely to be deprecated in favor of models from ASDC.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        service | No | array of :ref:`service <d_d3b85fa90604443b1743600895bf6c5f>` |  |  | 

.. _d_d2cbb2a0fa7d7e055e8c2c36caf49bdc:

vnf-image Model Structure
-------------------------

Image object that pertain to a VNF that doesn't have associated vservers.  This is a kludge.
###### Related Nodes
- FROM generic-vnf( generic-vnf Uses vnf-image, MANY2ONE, will delete target node)

-VNF-IMAGE cannot be deleted if related to GENERIC-VNF


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        application | Yes | string |  |  | The application that the image instantiates.
        application-vendor | Yes | string |  |  | The vendor of the application.
        application-version | No | string |  |  | The version of the application.
        relationship-list | No | array of :ref:`relationship <d_3a86fb483f7ec2bab2dad50fd3a6d612>` |  |  | 
        resource-version | No | string |  |  | Used for optimistic concurrency.  Must be empty on create, valid on update and delete.
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        vnf-image-uuid | Yes | string |  |  | Unique ID of this asset

.. _d_a6b4cd539322a835e6c128c46e480b01:

vnf-images Model Structure
--------------------------

Collection of image objects that pertain to a VNF that doesn't have associated vservers.  This is a kludge.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vnf-image | No | array of :ref:`vnf-image <d_d2cbb2a0fa7d7e055e8c2c36caf49bdc>` |  |  | 

