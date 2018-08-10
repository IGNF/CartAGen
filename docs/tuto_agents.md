# Tutorial on agent-based generalization with CartAGen
This tutorial explains how to use the agent-based generalization model implemented in CartAGen: [AGENT][2], [CartACom][3], [GAEL][4], and [DIOGEN][5]. Be aware that each of these implementations are for now mainly based on research code that has not been consolidated. Many bugs still remain and some functionalities described in the papers describing the models are not implemented in CartAGen.

> - Date 20/07/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.



The Basic Concepts of Agent-Based Generalization
-------------
To be done...

Generalizing data with AGENT
-------------
This tutorial explains how to quickly generalize the urban areas of the sample dataset. The first task is to load the sample dataset into CartAGen.

#### [](#header-4)Set the target scale

Then, we need to set the target scale of generalization. In this tutorial, we will set it to 1:50000 to have a significant simplification of the map.
To change the target scale, you need to open the generalization configuration frame by selecting it in the "Generalisation" menu (see image below).
In the frame, just change the scale and click on the validate button. The symbol sizes of roads, rivers, etc. should adjust to this target scale change, and you should see a clear need for generalization at this scale (symbols overlap).

![configuration menu](assets/images/cartagen_config_scale.png)  ![set the target scale](assets/images/cartagen_set_scale_50k.png)

#### [](#header-4)Set the AGENT parameters
The AGENT parameters allow the choice of the constraints that the agents will have to satisfy during generalization, and their relative importance.
To set the parameters, you need to open the dedicated frame by clicking on "AGENT configuration" in the "Generalisation" menu (see image below).

![AGENT configuration menu](assets/images/AGENT_configuration_menu.png)

The AGENT configuration frame contains several tabs that enable choosing the constraints for different themes of the map (see the "building" tab in the image below).
To keep it simple, we won't select all the constraints, only the following ones for buildings (size, granularity and squareness), blocks (micro satisfaction, density and proximity) and towns (block satisfaction, and street density).

![AGENT configuration frame](assets/images/AGENT_configuration_frame.png)

#### [](#header-4)Enrich the dataset
As usual in map generalization, the first step here is to enrich the dataset by creating the topology of the road networks, and by creating towns and blocks from the buildings and the road network.
To enrich the road network and build its topology (i.e. create road network nodes and the link between each road and its start and end node), in the "Themes" menu, click on "Enrichment" in the "Road network" submenu (see image below).

![enrich the road network with a topology](assets/images/agent_road_enrichment.png)

To create towns (from buildings, by dilation) and the blocks that cut towns with the faces of the road network, in the "Themes" menu, click on "Create town with buildings" in the "Town" submenu (see image below).

![create towns and blocks](assets/images/agent_enrichment.png)

The towns and blocks should appear as new layers of the map.

#### [](#header-4)Create the agents

For now, we only have geographic features in the CartAGen system, we need to create agents for each building, road, etc.
To create these agents, simply click on the "Create all agents" item in the "Agents/AGENT" menu (see image below).

![create AGENT agents from geographic features](assets/images/AGENT_create_agents.png)

Nothing should change in the GUI, but the agents do exist.
To verify that they exist, you can select any building with one of the selection buttons of the toolbar, and click on the "Load selection" button in the right panel.
This should add the agent to the list of the scheduler.

#### [](#header-4)Generalize a building
Select any building of the dataset. You have two possibilities to generalize it with the CartAGen GUI:
- click on the "Load selection" button in the right panel, and then click on the "run" button to start generalization.
- click on "Run generalization on selected agents" in the "Agents" menu (see image below).

![menu entry to run generalization on selected agents](assets/images/agents_run_menu.png)

#### [](#header-4)Generalize a block
This works similarly to the generalization of a building but select a block feature rather than a building feature.

#### [](#header-4)Generalize a town
This works similarly to the generalization of a building but select a town feature rather than a building feature.
The generalization might be quite longer in this case as a town might contain many agents.


> To go further in the use of AGENT, see [this advanced tutorial][10].

Generalizing data with CartACom
-------------
To be done...

> To go further in the use of CartACom, see [this advanced tutorial][11].

Generalizing data with GAEL
-------------
The implementation of the GAEL model is for now only partially functional in CartAGen.
To be done...

> To go further in the use of GAEL, see [this advanced tutorial][12].

Generalizing data with DIOGEN
-------------
To be done...


> To go further in the use of DIOGEN, see [this advanced tutorial][13].

See Also
-------------
- [tutorial on data loading][8]
- [tutorial to generalize loaded data][9]
- [tutorial on CartAGen data schema][7]
- [advanced tutorial on AGENT][10]
- [advanced tutorial on CartACom][11]
- [advanced tutorial on GAEL][12]
- [advanced tutorial on DIOGEN][13]
- [advanced tutorial on CollaGen][14]

- [Return to main page][6]


[1]: http://www.tandfonline.com/doi/abs/10.1080/13658810410001672881
[2]: http://icaci.org/files/documents/ICC_proceedings/ICC2001/icc2001/file/f13041.pdf
[3]: http://dx.doi.org/10.1080/13658816.2011.639302
[4]: https://www.researchgate.net/publication/221225232_Systeme_multi-agent_pour_la_deformation_en_generalisation_cartographique
[5]: http://www.tandfonline.com/doi/full/10.1080/23729333.2017.1300997
[6]: https://ignf.github.io/CartAGen
[7]: /tuto_agents.md
[8]: /tuto_import_data.md
[9]: /tuto_schema.md
[10]: /agents/AGENT_advanced.md
[11]: /agents/CartACom_advanced.md
[12]: /agents/GAEL_advanced.md
[13]: /agents/DIOGEN_advanced.md
[14]: /agents/CollaGen_advanced.md
