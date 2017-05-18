
CartAGen Project Steering Committee Guidelines
===================

This document describes the role and responsibilities of the CartAGen Project Steering Committee (PSC), as well as the processes under which it operates and takes decisions for the CartAGen project - both technical and non-technical.


> - Date 18/05/2017.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.

### Table of contents


[TOC]

----------


Structure
-------------
The PSC is made up of individuals who are meant to represent the various communities which have a stake in CartAGen. This may include core developers, advanced users and product end users, or infrastructure managers.

An odd number of participants is favored, to facilitate the voting process and help prevent ties. However, even with an odd number, the voting system may still allow for a tie in some cases. For this reason the PSC has an appointed Chair, whose sole responsibility is to break ties among the PSC.

CartAGen is a research platform, and most of developpers are expected to be researchers. So, turnover is allowed and expected, to accommodate people only able to become active on the project in intervals. A PSC member may step down at any time.

There is no set number of members for the PSC although the initial desire is to set the membership number at 5. At the beginning of the project, the number of members was set to three, to keep room for future communities that would be involved in CartAGen project.

PSC Responsibilities
-------------
The PSC is in charge of ensuring that the day-to-day operations of the CartAGen project run smoothly, as well as long term project management planning.

PSC members are expected to help guide the major development efforts of the project. This may include deciding which development efforts should receive priority when different efforts are in conflict, or vetoing a specific development effort if it does not serve the project on long term.

The PSC is responsible for defining project policies and practices.

The PSC is expected to be able to speak and act on behalf of the CartAGen project.

> **Examples of PSC management duties:**

> - setting the overall development road map.
> - developing technical standards and policies (e.g. coding standards, file naming conventions, etc...).
> - defining release policy and ensure regular releases (major and maintenance) of CartAGen software.
> - reviewing proposals for technical enhancements to the software.
> - ensuring the availability of the project infrastructure (e.g. git/GitHub, hosting options, etc...).
> - setting project priorities, especially with respect to project sponsorship.
> - creation and oversight of specialized sub-committees (e.g. project infrastructure, training) if some are created.
> - ensuring that Intellectual Property is respected.
> - managing commit access to the project master branch.

To ensure these tasks, the PSC uses dedicated procedures detailed below. In brief, the project team votes on proposals defined as GitHub issues on the cartagen repository. Proposals are available for review for at least one week before the voting process.

The Chair is the ultimate adjudicator if things break down.

Membership Responsibilities
-------------

####  Guiding Development

Members should take an active role guiding the development of new features they feel passionate about. Once a change request has been accepted and given a green light to proceed does not mean the members are free of their obligation. PSC members voting "+1" for a change request are expected to stay engaged and ensure the change is implemented and documented in a way that is most beneficial to users. Note that this applies not only to change requests that affect code, but also those that affect the web site, technical infrastructure, policies and standards.

#### Meeting Attendance

PSC members are expected to participate in pre-scheduled development meetings. As CartAGen is a research platform that seeks to involve international researchers, meetings might include videoconferencing. If known in advance that a member cannot attend a meeting, the member should let the meeting organizer know via e-mail.

#### Mailing List Participation

PSC members are expected to be active on both the users and development mailing lists, subject to open source mailing list etiquette. Non-developer members of the PSC are not expected to respond to coding level questions on the developer mailing list, however they are expected to provide their thoughts and opinions on user level requirements and compatibility issues when enhancement proposal discussions take place.

----------


Detailed voting Process
-------------------

The primary role of the PSC is to make decisions about project management. The following decision making process is used. It is based on the “Proposal-Vote” system.

Issues that require a vote decision are considered as enhancement proposals.

> **Note:**

>- Proposals may be made by any interested party (PSC, Non-PSC, committer, user, etc...)
>- Proposals should be addressed within 15 working days of being submitted, votes take place on issues created on the cartagen <i class="icon-provider-github"></i>GitHub repository, as well as discussions on the proposal, by any interested party, not just committee members
>- Anyone may comment and even cast votes on proposals on the issue, but only members of the Project Steering Committee's votes will be counted.
>- Each PSC member may vote one of the following in support of the proposal: 
>  -  +1 : For
>  -  -1 : Against
>  - +0: Mildly for, but mostly indifferent
>  - -0: Mildly against, but mostly indifferent
> - A -1 vote must be accompanied with a detailed reason of being against.
> - A vote is *successful* if there is a majority of positive votes.
> - A vote is *unanimous*, if there are either: 
>  - No -1 votes against it, or
>  - No +1 votes for it.
> - In the event of a *successful non unanimous* vote, the following steps are taken: 
>  - Each member who votes -1 *may* supply an alternative which the original author can use to rework the proposal in order to satisfy that PSC member.
>   - If at least one -1 voting PSC member supplies some alternative criteria, the original author must rework the proposal and resubmit, and the voting process starts again from scratch.
>  - If no -1 voters are able to supply alternative criteria, the proposal is accepted.
>  - In the event of an *unsuccessful* vote, the author may rework and submit. A proposal may not be resubmitted after being rejected three times.
> - Note that a majority of positive votes does not need to be a majority of the full PSC, just a majority of the ‘active’ PSC - defined by those responding within 4 working days on the issue. PSC members need not sound in on every single motion, but are expected to be active.
> - Upon completion of discussion and voting the author should announce whether they are proceeding (proposal accepted) or are withdrawing their proposal (vetoed).

When is Vote Required?
-------------------

**A vote is required generally for**:
- any action that has a major effect on others in the CartAGen communityany action that would break backwards compatibility
- any action that would change core code in a significant manner

> *For instance, a vote is required for*:

> - Any change to committee membership (new members, removing inactive members).
> - Changes to project infrastructure (e.g. tool, location or substantive configuration).
> - Adding substantial amounts of new codeChanging the core interfaces of the project (e.g. core agents, core generalization objetcs).
> - When releases should take place.
> - Anything dealing with relationships with external entities.
> - Anything that might be controversial.

**A vote is NOT required for**:

- Improvements that can go in a community module (i.e. not the master branch)
- Small changes to the code.

For minor decisions where feedback might be desired, the course of action to take is to consult the development list or raise it in a meeting. The CartAGen Project recognizes that it is run by those who are
actually doing the work, and thus we want to avoid high overhead for ‘getting things done’.

----------


PSC evolution
-------------

The PSC is not a committee with fixed duration mandates, member can be added or removed at any time.

#### Adding Members

Any member of the CartAGen development mailing list may nominate someone for committee membership at any time. Only existing PSC committee members may vote on new members. Nominees must receive a majority vote from existing members to be added to the PSC.

#### Stepping Down

If for any reason a PSC member is not able to fully participate then they certainly are free to step down. If a member is not active (e.g. no voting, no meeting or email participation) for a period of six months then the committee reserves the right to seek nominations to fill that position. Should that person become active again then they would certainly be welcome, but would require a nomination.

----------


Bootstrapping
--------------------

This document has been approved by the initial members of the CartAGen project.

> It has been decided that the following person will form the initial committee :

> - Cécile Duchêne ( firstname.lastname@ign.fr )
> - Imran Lokhat (firstname.lastname@ign.fr )
> - Guillaume Touya (firstname.lastname@ign.fr )

Guillaume Touya is declared initial chair of the CartAGen PSC.

----------


References
--------------------

This document has been inspired by and adapted from :

- iTowns PSC document : https://github.com/iTowns/itowns-project/blob/master/PSC.md 
- Mapserver PSC document : https://raw.githubusercontent.com/mapserver/docs/branch-7-0/en/development/rfc/ms-rfc-23.txtGeoserver 
- PSC document : http://docs.geoserver.org/2.5.x/en/developer/policies/psc.html


  [1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
