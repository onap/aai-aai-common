.. contents::
   :depth: 3
..

Edge Rules
==========

-  `Edge Rules Location <#EdgeRules-EdgeRulesLocation>`__

-  `How to Interpret an Edge
   Rule <#EdgeRules-HowtoInterpretanEdgeRule>`__

   -  `Mentally Constructing an
      Edge <#EdgeRules-MentallyConstructinganEdge>`__

      -  `Quick guide for which is your in node and which is your out
         node <#EdgeRules-Quickguideforwhichisyourinnod>`__

-  `How to Read Multiplicity <#EdgeRules-HowtoReadMultiplicity>`__

-  `Internal Edge Properties <#EdgeRules-InternalEdgeProperties>`__

   -  `Quick guide to our direction
      syntax: <#EdgeRules-Quickguidetoourdirectionsynta>`__

Edge Rules Location
-------------------

The edge rules json files are located in
aai-common/aai-core/src/main/resources/dbedgerules.

How to Interpret an Edge Rule
-----------------------------

Mentally Constructing an Edge
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Read the from/to/direction as a recipe for how to construct the edge.

{

    | "from": "tenant",
    | "to": "vserver",
    | "label": "owns",
    | "direction": "OUT",
    | "multiplicity": "One2Many",
    | "contains-other-v": "${direction}",
    | "delete-other-v": "NONE",
    | "SVC-INFRA": "!${direction}",
    | "prevent-delete": "${direction}"

}

1. Start by drawing the "from" node.

2. Draw an edge off this node in the specified. So if it's OUT, point
   the edge out away from the from node, if it's IN, point it into the
   node.

3. Draw the "to" node on the empty end of that edge.

Essentially, "from" and "to" do not imply direction. Think of them as
more like "NodeA" and "NodeB".

Quick guide for which is your in node and which is your out node
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

+-----------------+---------------+----------------+
| **direction**   | **in node**   | **out node**   |
+=================+===============+================+
| OUT             | to            | from           |
+-----------------+---------------+----------------+
| IN              | from          | to             |
+-----------------+---------------+----------------+

How to Read Multiplicity
------------------------

Multiplicity, by itself, has nothing to do with the edge direction or
the from/to nodes. Think of it as a property of the edge label. It
defines how many IN edges of this label and how many OUT edges of this
label are allowed. The format we use is defined by Titan to be In2Out.

Ex: One2Many means on a node, there may be only one IN edge, and many
OUT edges.

The from and to node types come in on top of this at the A&AI layer.
A&AI's code defines which node types may have those IN and OUT edges of
this label.

{

    | "from": "tenant",
    | "to": "vserver",
    | "label": "owns",
    | "direction": "OUT",
    | "multiplicity": "One2Many",
    | "contains-other-v": "${direction}",
    | "delete-other-v": "NONE",
    | "SVC-INFRA": "!${direction}",
    | "prevent-delete": "${direction}"

}

In this example, the vserver gets the IN edge, so it may have only one
edge from a tenant. The tenant gets the OUT edge, so it may get many
edges to vservers.

Internal Edge Properties
------------------------

A&AI uses the following edge properties for internal processing.

-  contains-other-v

   -  This property defines whether or not the other vertex is contained
      within another when rendering the resources view

   -  This property was previously known as isParent

   -  If contains-other-v=OUT, this means that the outVertex contains
      the inVertex

   -  Or in other words, you can read contains-other-v=OUT as "I am an
      edge, my OUT vertex contains my IN vertex"

-  delete-other-v

   -  defines whether or not the other vertex is automatically included
      in delete processing

   -  this property was previously known as hasDelTarget

   -  if delete-other-v=IN, this means that when deleting the inVertex
      also delete the outVertex

-  SVC-INFRA

   -  what direction should the traverser continue in when running
      edge-tag-query

   -  if SVC-INFRA=OUT, when on the outVertex traverse to the inVertex
      and continue

-  prevent-delete

   -  defines whether or not this edge can be deleted from a particular
      direction

   -  if prevent-delete=IN, prevent the deletion of the inVertex of the
      edge, allow the outVertex to be deleted.

   -  Or in other words, you can read it as "I am an edge, my IN vertex
      cannot be deleted"

Quick guide to our direction syntax:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

${direction} = same as value of "direction" property

!${direction} = opposite
