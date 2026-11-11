package dev.nohus.rift.notifications.eve

import dev.nohus.rift.types.TypeDetail
import dev.nohus.rift.types.TypeDetails
import dev.nohus.rift.types.getDisplayName
import org.koin.core.annotation.Single

/**
 * Selects the template associated with each EVE notification type and formats it accordingly
 */
@Single
class EveNotificationTemplates(
    private val eveNotificationTemplateFormatter: EveNotificationTemplateFormatter
) {

    fun format(
        normalizedType: String,
        inputData: Map<String, Any?>,
        typeDetails: TypeDetails
    ): ParsedEveNotification {
        val data = inputData + (EVE_NOTIFICATION_TYPE to normalizedType)
        return when (normalizedType) {
            "acceptedally" -> formatAcceptedAlly(data, typeDetails)
            "acceptedsurrender" -> formatAcceptedSurrender(data, typeDetails)
            "agentretiredtrigravian" -> formatAgentRetiredTrigravian(data, typeDetails)
            "allanchoringmsg" -> formatAllAnchoringMsg(data, typeDetails)
            "allmaintenancebillmsg" -> formatAllMaintenanceBillMsg(data, typeDetails)
            "allstrucinvulnerablemsg" -> formatAllStrucInvulnerableMsg(data, typeDetails)
            "allstructvulnerablemsg" -> formatAllStructVulnerableMsg(data, typeDetails)
            "allwarcorpjoinedalliancemsg" -> formatAllWarCorpJoinedAllianceMsg(data, typeDetails)
            "allwardeclaredmsg" -> formatAllWarDeclaredMsg(data, typeDetails)
            "allwarinvalidatedmsg" -> formatAllWarInvalidatedMsg(data, typeDetails)
            "allwarretractedmsg" -> formatAllWarRetractedMsg(data, typeDetails)
            "allwarsurrendermsg" -> formatAllWarSurrenderMsg(data, typeDetails)
            "alliancecapitalchanged" -> formatAllianceCapitalChanged(data, typeDetails)
            "alliancewardeclaredv2" -> formatAllianceWarDeclaredV2(data, typeDetails)
            "allycontractcancelled" -> formatAllyContractCancelled(data, typeDetails)
            "allyjoinedwaraggressormsg" -> formatAllyJoinedWarAggressorMsg(data, typeDetails)
            "allyjoinedwarallymsg" -> formatAllyJoinedWarAllyMsg(data, typeDetails)
            "allyjoinedwardefendermsg" -> formatAllyJoinedWarDefenderMsg(data, typeDetails)
            "battlepunishfriendlyfire" -> formatBattlePunishFriendlyFire(data, typeDetails)
            "billoutofmoneymsg" -> formatBillOutOfMoneyMsg(data, typeDetails)
            "billpaidcorpallmsg" -> formatBillPaidCorpAllMsg(data, typeDetails)
            "bountyclaimmsg" -> formatBountyClaimMsg(data, typeDetails)
            "bountyplacedalliance" -> formatBountyPlacedAlliance(data, typeDetails)
            "bountyplacedchar" -> formatBountyPlacedChar(data, typeDetails)
            "bountyplacedcorp" -> formatBountyPlacedCorp(data, typeDetails)
            "bountyyourbountyclaimed" -> formatBountyYourBountyClaimed(data, typeDetails)
            "buddyconnectcontactadd" -> formatBuddyConnectContactAdd(data, typeDetails)
            "charappacceptmsg" -> formatCharAppAcceptMsg(data, typeDetails)
            "charapprejectmsg" -> formatCharAppRejectMsg(data, typeDetails)
            "charappwithdrawmsg" -> formatCharAppWithdrawMsg(data, typeDetails)
            "charleftcorpmsg" -> formatCharLeftCorpMsg(data, typeDetails)
            "charmedalmsg" -> formatCharMedalMsg(data, typeDetails)
            "charterminationmsg" -> formatCharTerminationMsg(data, typeDetails)
            "cloneactivationmsg" -> formatCloneActivationMsg(data, typeDetails)
            "cloneactivationmsg2" -> formatCloneActivationMsg2(data, typeDetails)
            "clonemovedmsg" -> formatCloneMovedMsg(data, typeDetails)
            "clonerevokedmsg1" -> formatCloneRevokedMsg1(data, typeDetails)
            "clonerevokedmsg2" -> formatCloneRevokedMsg2(data, typeDetails)
            "contactadd" -> formatContactAdd(data, typeDetails)
            "contactedit" -> formatContactEdit(data, typeDetails)
            "containerpasswordmsg" -> formatContainerPasswordMsg(data, typeDetails)
            "contractregionchangedtopochven" -> formatContractRegionChangedToPochven(data, typeDetails)
            "corpallbillmsg" -> formatCorpAllBillMsg(data, typeDetails)
            "corpappacceptmsg" -> formatCorpAppAcceptMsg(data, typeDetails)
            "corpappinvitedmsg" -> formatCorpAppInvitedMsg(data, typeDetails)
            "corpappnewmsg" -> formatCorpAppNewMsg(data, typeDetails)
            "corpapprejectcustommsg" -> formatCorpAppRejectCustomMsg(data, typeDetails)
            "corpapprejectmsg" -> formatCorpAppRejectMsg(data, typeDetails)
            "corpbecamewareligible" -> formatCorpBecameWarEligible(data, typeDetails)
            "corpdividendmsg" -> formatCorpDividendMsg(data, typeDetails)
            "corpfriendlyfiredisabletimercompleted" -> formatCorpFriendlyFireDisableTimerCompleted(data, typeDetails)
            "corpfriendlyfiredisabletimerstarted" -> formatCorpFriendlyFireDisableTimerStarted(data, typeDetails)
            "corpfriendlyfireenabletimercompleted" -> formatCorpFriendlyFireEnableTimerCompleted(data, typeDetails)
            "corpfriendlyfireenabletimerstarted" -> formatCorpFriendlyFireEnableTimerStarted(data, typeDetails)
            "corpkicked" -> formatCorpKicked(data, typeDetails)
            "corpliquidationmsg" -> formatCorpLiquidationMsg(data, typeDetails)
            "corpnewceomsg" -> formatCorpNewCEOMsg(data, typeDetails)
            "corpnewsmsg" -> formatCorpNewsMsg(data, typeDetails)
            "corpnolongerwareligible" -> formatCorpNoLongerWarEligible(data, typeDetails)
            "corpofficeexpirationmsg" -> formatCorpOfficeExpirationMsg(data, typeDetails)
            "corpstructlostmsg" -> formatCorpStructLostMsg(data, typeDetails)
            "corptaxchangemsg" -> formatCorpTaxChangeMsg(data, typeDetails)
            "corpvoteceorevokedmsg" -> formatCorpVoteCEORevokedMsg(data, typeDetails)
            "corpvotemsg" -> formatCorpVoteMsg(data, typeDetails)
            "corpwardeclaredmsg" -> formatCorpWarDeclaredMsg(data, typeDetails)
            "corpwardeclaredv2" -> formatCorpWarDeclaredV2(data, typeDetails)
            "corpwarfightinglegalmsg" -> formatCorpWarFightingLegalMsg(data, typeDetails)
            "corpwarinvalidatedmsg" -> formatCorpWarInvalidatedMsg(data, typeDetails)
            "corpwarretractedmsg" -> formatCorpWarRetractedMsg(data, typeDetails)
            "corpwarsurrendermsg" -> formatCorpWarSurrenderMsg(data, typeDetails)
            "corporationgoalclosed" -> formatCorporationGoalClosed(data, typeDetails)
            "corporationgoalcompleted" -> formatCorporationGoalCompleted(data, typeDetails)
            "corporationgoalcreated" -> formatCorporationGoalCreated(data, typeDetails)
            "corporationgoalexpired" -> formatCorporationGoalExpired(data, typeDetails)
            "corporationgoallimitreached" -> formatCorporationGoalLimitReached(data, typeDetails)
            "corporationgoalnamechange" -> formatCorporationGoalNameChange(data, typeDetails)
            "corporationleft" -> formatCorporationLeft(data, typeDetails)
            "customsmsg" -> formatCustomsMsg(data, typeDetails)
            "dailyitemrewardautoclaimed" -> formatDailyItemRewardAutoClaimed(data, typeDetails)
            "declarewar" -> formatDeclareWar(data, typeDetails)
            "districtattacked" -> formatDistrictAttacked(data, typeDetails)
            "dustappacceptedmsg" -> formatDustAppAcceptedMsg(data, typeDetails)
            "essmainbanklink" -> formatESSMainBankLink(data, typeDetails)
            "entosiscapturestarted" -> formatEntosisCaptureStarted(data, typeDetails)
            "expertsystemexpired" -> formatExpertSystemExpired(data, typeDetails)
            "fwalliancekickmsg" -> formatFWAllianceKickMsg(data, typeDetails)
            "fwalliancewarningmsg" -> formatFWAllianceWarningMsg(data, typeDetails)
            "fwcharkickmsg" -> formatFWCharKickMsg(data, typeDetails)
            "fwcharrankgainmsg" -> formatFWCharRankGainMsg(data, typeDetails)
            "fwcharranklossmsg" -> formatFWCharRankLossMsg(data, typeDetails)
            "fwcharwarningmsg" -> formatFWCharWarningMsg(data, typeDetails)
            "fwcorpjoinmsg" -> formatFWCorpJoinMsg(data, typeDetails)
            "fwcorpkickmsg" -> formatFWCorpKickMsg(data, typeDetails)
            "fwcorpleavemsg" -> formatFWCorpLeaveMsg(data, typeDetails)
            "fwcorpwarningmsg" -> formatFWCorpWarningMsg(data, typeDetails)
            "facwarcorpjoinrequestmsg" -> formatFacWarCorpJoinRequestMsg(data, typeDetails)
            "facwarcorpjoinwithdrawmsg" -> formatFacWarCorpJoinWithdrawMsg(data, typeDetails)
            "facwarcorpleaverequestmsg" -> formatFacWarCorpLeaveRequestMsg(data, typeDetails)
            "facwarcorpleavewithdrawmsg" -> formatFacWarCorpLeaveWithdrawMsg(data, typeDetails)
            "facwardirectenlistmentrevoked" -> formatFacWarDirectEnlistmentRevoked(data, typeDetails)
            "facwarlpdisqualifiedevent" -> formatFacWarLPDisqualifiedEvent(data, typeDetails)
            "facwarlpdisqualifiedkill" -> formatFacWarLPDisqualifiedKill(data, typeDetails)
            "facwarlppayoutevent" -> formatFacWarLPPayoutEvent(data, typeDetails)
            "facwarlppayoutkill" -> formatFacWarLPPayoutKill(data, typeDetails)
            "freelanceprojectacldeleted" -> formatFreelanceProjectACLDeleted(data, typeDetails)
            "freelanceprojectclosed" -> formatFreelanceProjectClosed(data, typeDetails)
            "freelanceprojectcompleted" -> formatFreelanceProjectCompleted(data, typeDetails)
            "freelanceprojectcreated" -> formatFreelanceProjectCreated(data, typeDetails)
            "freelanceprojectexpired" -> formatFreelanceProjectExpired(data, typeDetails)
            "freelanceprojectlimitreached" -> formatFreelanceProjectLimitReached(data, typeDetails)
            "freelanceprojectparticipantkicked" -> formatFreelanceProjectParticipantKicked(data, typeDetails)
            "gametimeadded" -> formatGameTimeAdded(data, typeDetails)
            "gametimereceived" -> formatGameTimeReceived(data, typeDetails)
            "gametimesent" -> formatGameTimeSent(data, typeDetails)
            "giftreceived" -> formatGiftReceived(data, typeDetails)
            "ihubdestroyedbybillfailure" -> formatIHubDestroyedByBillFailure(data, typeDetails)
            "incursioncompletedmsg" -> formatIncursionCompletedMsg(data, typeDetails)
            "industryteamauctionlost" -> formatIndustryTeamAuctionLost(data, typeDetails)
            "industryteamauctionwon" -> formatIndustryTeamAuctionWon(data, typeDetails)
            "infrastructurehubbillabouttoexpire" -> formatInfrastructureHubBillAboutToExpire(data, typeDetails)
            "insuranceexpirationmsg" -> formatInsuranceExpirationMsg(data, typeDetails)
            "insurancefirstshipmsg" -> formatInsuranceFirstShipMsg(data, typeDetails)
            "insuranceinvalidatedmsg" -> formatInsuranceInvalidatedMsg(data, typeDetails)
            "insuranceissuedmsg" -> formatInsuranceIssuedMsg(data, typeDetails)
            "insurancepayoutmsg" -> formatInsurancePayoutMsg(data, typeDetails)
            "invasioncompletedmsg" -> formatInvasionCompletedMsg(data, typeDetails)
            "invasionsystemlogin" -> formatInvasionSystemLogin(data, typeDetails)
            "invasionsystemstart" -> formatInvasionSystemStart(data, typeDetails)
            "jumpclonedeletedmsg1" -> formatJumpCloneDeletedMsg1(data, typeDetails)
            "jumpclonedeletedmsg2" -> formatJumpCloneDeletedMsg2(data, typeDetails)
            "killreportfinalblow" -> formatKillReportFinalBlow(data, typeDetails)
            "killreportvictim" -> formatKillReportVictim(data, typeDetails)
            "killrightavailable" -> formatKillRightAvailable(data, typeDetails)
            "killrightavailableopen" -> formatKillRightAvailableOpen(data, typeDetails)
            "killrightearned" -> formatKillRightEarned(data, typeDetails)
            "killrightunavailable" -> formatKillRightUnavailable(data, typeDetails)
            "killrightunavailableopen" -> formatKillRightUnavailableOpen(data, typeDetails)
            "killrightused" -> formatKillRightUsed(data, typeDetails)
            "lpautoredeemed" -> formatLPAutoRedeemed(data, typeDetails)
            "locatecharmsg" -> formatLocateCharMsg(data, typeDetails)
            "madewarmutual" -> formatMadeWarMutual(data, typeDetails)
            "mercofferretractedmsg" -> formatMercOfferRetractedMsg(data, typeDetails)
            "mercofferednegotiationmsg" -> formatMercOfferedNegotiationMsg(data, typeDetails)
            "mercenarydenattacked" -> formatMercenaryDenAttacked(data, typeDetails)
            "mercenarydennewmto" -> formatMercenaryDenNewMTO(data, typeDetails)
            "mercenarydenreinforced" -> formatMercenaryDenReinforced(data, typeDetails)
            "missioncanceledtriglavian" -> formatMissionCanceledTriglavian(data, typeDetails)
            "missionofferexpirationmsg" -> formatMissionOfferExpirationMsg(data, typeDetails)
            "missiontimeoutmsg" -> formatMissionTimeoutMsg(data, typeDetails)
            "moonminingautomaticfracture" -> formatMoonminingAutomaticFracture(data, typeDetails)
            "moonminingextractioncancelled" -> formatMoonminingExtractionCancelled(data, typeDetails)
            "moonminingextractionfinished" -> formatMoonminingExtractionFinished(data, typeDetails)
            "moonminingextractionstarted" -> formatMoonminingExtractionStarted(data, typeDetails)
            "moonmininglaserfired" -> formatMoonminingLaserFired(data, typeDetails)
            "mutualwarexpired" -> formatMutualWarExpired(data, typeDetails)
            "mutualwarinviteaccepted" -> formatMutualWarInviteAccepted(data, typeDetails)
            "mutualwarinviterejected" -> formatMutualWarInviteRejected(data, typeDetails)
            "mutualwarinvitesent" -> formatMutualWarInviteSent(data, typeDetails)
            "npcstandingschanged" -> formatNPCStandingsChanged(data, typeDetails)
            "npcstandingsgained" -> formatNPCStandingsGained(data, typeDetails)
            "npcstandingslost" -> formatNPCStandingsLost(data, typeDetails)
            "offertoallyretracted" -> formatOfferToAllyRetracted(data, typeDetails)
            "offeredsurrender" -> formatOfferedSurrender(data, typeDetails)
            "offeredtoally" -> formatOfferedToAlly(data, typeDetails)
            "officeleasecanceledinsufficientstandings" -> formatOfficeLeaseCanceledInsufficientStandings(data, typeDetails)
            "oldlscmessages" -> formatOldLscMessages(data, typeDetails)
            "orbitalattacked" -> formatOrbitalAttacked(data, typeDetails)
            "orbitalreinforced" -> formatOrbitalReinforced(data, typeDetails)
            "ownershiptransferred" -> formatOwnershipTransferred(data, typeDetails)
            "rafflecreated" -> formatRaffleCreated(data, typeDetails)
            "raffleexpired" -> formatRaffleExpired(data, typeDetails)
            "rafflefinished" -> formatRaffleFinished(data, typeDetails)
            "reimbursementmsg" -> formatReimbursementMsg(data, typeDetails)
            "researchmissionavailablemsg" -> formatResearchMissionAvailableMsg(data, typeDetails)
            "retractswar" -> formatRetractsWar(data, typeDetails)
            "spautoredeemed" -> formatSPAutoRedeemed(data, typeDetails)
            "seasonalchallengecompleted" -> formatSeasonalChallengeCompleted(data, typeDetails)
            "skinsequencingcompleted" -> formatSkinSequencingCompleted(data, typeDetails)
            "skyhookdeployed" -> formatSkyhookDeployed(data, typeDetails)
            "skyhookdestroyed" -> formatSkyhookDestroyed(data, typeDetails)
            "skyhooklostshields" -> formatSkyhookLostShields(data, typeDetails)
            "skyhookonline" -> formatSkyhookOnline(data, typeDetails)
            "skyhookunderattack" -> formatSkyhookUnderAttack(data, typeDetails)
            "sovallclaimaquiredmsg" -> formatSovAllClaimAquiredMsg(data, typeDetails)
            "sovallclaimlostmsg" -> formatSovAllClaimLostMsg(data, typeDetails)
            "sovcommandnodeeventstarted" -> formatSovCommandNodeEventStarted(data, typeDetails)
            "sovcorpbilllatemsg" -> formatSovCorpBillLateMsg(data, typeDetails)
            "sovcorpclaimfailmsg" -> formatSovCorpClaimFailMsg(data, typeDetails)
            "sovdisruptormsg" -> formatSovDisruptorMsg(data, typeDetails)
            "sovstationenteredfreeport" -> formatSovStationEnteredFreeport(data, typeDetails)
            "sovstructuredestroyed" -> formatSovStructureDestroyed(data, typeDetails)
            "sovstructurereinforced" -> formatSovStructureReinforced(data, typeDetails)
            "sovstructureselfdestructcancel" -> formatSovStructureSelfDestructCancel(data, typeDetails)
            "sovstructureselfdestructfinished" -> formatSovStructureSelfDestructFinished(data, typeDetails)
            "sovstructureselfdestructrequested" -> formatSovStructureSelfDestructRequested(data, typeDetails)
            "sovereigntyihdamagemsg" -> formatSovereigntyIHDamageMsg(data, typeDetails)
            "sovereigntysbudamagemsg" -> formatSovereigntySBUDamageMsg(data, typeDetails)
            "sovereigntytcudamagemsg" -> formatSovereigntyTCUDamageMsg(data, typeDetails)
            "stationaggressionmsg1" -> formatStationAggressionMsg1(data, typeDetails)
            "stationaggressionmsg2" -> formatStationAggressionMsg2(data, typeDetails)
            "stationconquermsg" -> formatStationConquerMsg(data, typeDetails)
            "stationservicedisabled" -> formatStationServiceDisabled(data, typeDetails)
            "stationserviceenabled" -> formatStationServiceEnabled(data, typeDetails)
            "stationstatechangemsg" -> formatStationStateChangeMsg(data, typeDetails)
            "storylinemissionavailablemsg" -> formatStoryLineMissionAvailableMsg(data, typeDetails)
            "structureanchoring" -> formatStructureAnchoring(data, typeDetails)
            "structurecouriercontractchanged" -> formatStructureCourierContractChanged(data, typeDetails)
            "structuredestroyed" -> formatStructureDestroyed(data, typeDetails)
            "structurefuelalert" -> formatStructureFuelAlert(data, typeDetails)
            "structureimpendingabandonmentassetsatrisk" -> formatStructureImpendingAbandonmentAssetsAtRisk(data, typeDetails)
            "structureitemsdelivered" -> formatStructureItemsDelivered(data, typeDetails)
            "structureitemsmovedtosafety" -> formatStructureItemsMovedToSafety(data, typeDetails)
            "structurelostarmor" -> formatStructureLostArmor(data, typeDetails)
            "structurelostshields" -> formatStructureLostShields(data, typeDetails)
            "structurelowreagentsalert" -> formatStructureLowReagentsAlert(data, typeDetails)
            "structurenoreagentsalert" -> formatStructureNoReagentsAlert(data, typeDetails)
            "structureonline" -> formatStructureOnline(data, typeDetails)
            "structurepaintpurchased" -> formatStructurePaintPurchased(data, typeDetails)
            "structureservicesoffline" -> formatStructureServicesOffline(data, typeDetails)
            "structureunanchoring" -> formatStructureUnanchoring(data, typeDetails)
            "structureunderattack" -> formatStructureUnderAttack(data, typeDetails)
            "structurewenthighpower" -> formatStructureWentHighPower(data, typeDetails)
            "structurewentlowpower" -> formatStructureWentLowPower(data, typeDetails)
            "structuresjobscancelled" -> formatStructuresJobsCancelled(data, typeDetails)
            "structuresjobspaused" -> formatStructuresJobsPaused(data, typeDetails)
            "structuresreinforcementchanged" -> formatStructuresReinforcementChanged(data, typeDetails)
            "toweralertmsg" -> formatTowerAlertMsg(data, typeDetails)
            "towerresourcealertmsg" -> formatTowerResourceAlertMsg(data, typeDetails)
            "transactionreversalmsg" -> formatTransactionReversalMsg(data, typeDetails)
            "tutorialmsg" -> formatTutorialMsg(data, typeDetails)
            "waradopted" -> formatWarAdopted(data, typeDetails)
            "warallyinherited" -> formatWarAllyInherited(data, typeDetails)
            "warallyofferdeclinedmsg" -> formatWarAllyOfferDeclinedMsg(data, typeDetails)
            "warconcordinvalidates" -> formatWarConcordInvalidates(data, typeDetails)
            "wardeclared" -> formatWarDeclared(data, typeDetails)
            "warendedhqsecuritydrop" -> formatWarEndedHqSecurityDrop(data, typeDetails)
            "warhqremovedfromspace" -> formatWarHQRemovedFromSpace(data, typeDetails)
            "warinherited" -> formatWarInherited(data, typeDetails)
            "warinvalid" -> formatWarInvalid(data, typeDetails)
            "warretracted" -> formatWarRetracted(data, typeDetails)
            "warretractedbyconcord" -> formatWarRetractedByConcord(data, typeDetails)
            "warsurrenderdeclinedmsg" -> formatWarSurrenderDeclinedMsg(data, typeDetails)
            "warsurrenderoffermsg" -> formatWarSurrenderOfferMsg(data, typeDetails)
            else -> formatGeneric(data)
        }
    }

    private fun formatGeneric(data: Map<String, Any?>): ParsedEveNotification {
        return eveNotificationTemplateFormatter.formatGeneric(data)
    }

    private fun formatDocumented(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        subject: String,
        body: String? = null,
    ): ParsedEveNotification {
        return eveNotificationTemplateFormatter.formatDocumented(data, typeDetails, subject, body)
    }

    private fun formatContact(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        subject: String,
        body: String? = null,
    ): ParsedEveNotification {
        return eveNotificationTemplateFormatter.formatContact(data, typeDetails, subject, body)
    }

    private fun formatMoonMining(
        data: Map<String, Any?>,
        typeDetails: TypeDetails,
        subject: String,
        body: String,
    ): ParsedEveNotification {
        val ores = (data["oreVolumeByType"] as? Map<*, *>)
            ?.mapNotNull { [rawTypeId, rawVolume] ->
                val typeId = rawTypeId.asLong() ?: return@mapNotNull null
                val volume = rawVolume.asDouble() ?: return@mapNotNull null
                val name = typeDetails[typeId]?.getDisplayName() ?: typeId.toString()
                Triple(name, typeId, volume)
            }
            ?.sortedBy { it.first }
            .orEmpty()
        val oreData = mutableMapOf<String, Any?>()
        val oreComposition = ores.mapIndexed { index, ore ->
            val typeField = "ore${index}TypeID"
            val volumeField = "ore${index}Volume"
            oreData[typeField] = ore.second
            oreData[volumeField] = ore.third
            "{[item]$typeField.name, linkify} {[numeric]$volumeField, decimalPlaces=0, leadingZeroes=0, useGrouping} m³"
        }.joinToString("<br>")
        return formatDocumented(data + oreData, typeDetails, subject, body.replace("{oreComp}", oreComposition))
    }

    /**
     * Fields:
     * - `allyID`: `123456`; type: Integer.
     * - `charID`: `123456`; type: Integer.
     * - `enemyID`: `123456`; type: Integer.
     * - `iskValue`: `0.0`; type: Float.
     * - `time`: `132537600000000000`; type: Integer.
     */
    private fun formatAcceptedAlly(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "You have accepted an ally in your war against {enemyName}", "{charName} has accepted {allyName} as an ally in your war against {enemyName}, for {iskOffer}. They will join the war at {joinTime}.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `entityID`: `123456`; type: Integer.
     * - `iskValue`: `0.0`; type: Float.
     * - `offeringID`: `123456`; type: Integer.
     */
    private fun formatAcceptedSurrender(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{entityName} has accepted a surrender offer", "{charName} has accepted surrender offer from {offeringName}. The war will end in 24 hours. You received {iskOffer} as part of the surrender.")

    private fun formatAgentRetiredTrigravian(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Agent Unavailable Due to Evacuation", "Your former contact has been evacuated from a Triglavian-controlled system and is no longer available to provide freelance capsuleers with work.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `allianceID`: `123456`; type: Integer.
     * - `corpsPresent`: `[]`; type: Collection.
     */
    private fun formatAllAnchoringMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: Control tower anchored in {[location]solarSystemID.name}", "<br>System: {[location]solarSystemID.name, linkify}<br><br>Moon: {[location]moonID.name, linkify}, Type: {[item]typeID.name, linkify}<br>Corp: {corpName}{allianceText}<br>{otherTowersText}")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `dueDate`: `132537600000000000`; type: Integer.
     */
    private fun formatAllMaintenanceBillMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Due maintenance bill", "Maintenance bill for {allianceName} is due for payment on {[datetime]dueDate, date=full, time=none}. Please make sure the bill is paid in time, otherwise the alliance will be automatically disbanded.")

    private fun formatAllStrucInvulnerableMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Sovereignty claim stable.", "The link to the majority of stargates in system {[location]solarSystemID.name, linkify} has been restored.")

    private fun formatAllStructVulnerableMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "ALERT: Sovereignty claim is vulnerable to attack!", "Your sovereignty hub and outposts in {[location]solarSystemID.name, linkify} are vulnerable to attack, as the connections to the system's stargates have been disrupted by one or more Sovereignty Blockade Units.<br><br>Re-establish the connection(s) to the system's stargate(s) to retain sovereignty.")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatAllWarCorpJoinedAllianceMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation you are at war with is joining an alliance", "{corporation} is joining {alliance} alliance. Since you are at war with {corporation}, in 24 hours you will also be at war with {alliance}.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     * - `delayHours`: `123456`; type: Integer.
     * - `hostileState`: `123456`; type: Integer.
     */
    private fun formatAllWarDeclaredMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatWarDeclared(data, typeDetails)

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     * - `delayHours`: `123456`; type: Integer.
     * - `hostileState`: `123456`; type: Integer.
     */
    private fun formatAllWarInvalidatedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "CONCORD invalidates war declared by {declaredByName} against {againstName}", "CONCORD has declared this war invalid as it breaches one or more articles in the Yulai Convention. The war will be declared as being over after approximately 24 hours.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     * - `delayHours`: `123456`; type: Integer.
     * - `hostileState`: `123456`; type: Integer.
     */
    private fun formatAllWarRetractedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{declaredByName} Retracts War Against {againstName}", "The war between {declaredByName} and {againstName} is coming to an end. {declaredByName} has retracted the war against {againstName}. The war will be declared as being over after approximately 24 hours.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     * - `delayHours`: `123456`; type: Integer.
     * - `hostileState`: `123456`; type: Integer.
     */
    private fun formatAllWarSurrenderMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{againstName} surrenders to {declaredByName}", "The war between {againstName} and {declaredByName} is coming to an end. {againstName} has surrendered to {declaredByName}. The war will be declared as being over after approximately 24 hours and neither organizations will be able to declare new wars against each other for the next 2 weeks.")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatAllianceCapitalChanged(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[location]solarSystemID.name, linkify} is now the Capital System of {allianceName}", "This system is now your Alliance Capital and all of {allianceName}'s sovereignty structures within it receive a defensive bonus.")

    private fun formatAllianceWarDeclaredV2(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatWarDeclared(data, typeDetails)

    /**
     * Fields:
     * - `aggressorID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `timeFinished`: `132537600000000000`; type: Integer.
     */
    private fun formatAllyContractCancelled(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Ally agreement with {defender} has been cancelled", "Your ally agreement with {defender} in its war against {aggressor} has been cancelled due to {defender} flagging the war as mutual. Your participation in this war will end in 24 hours ({time}).")

    /**
     * Fields:
     * - `allyID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `startTime`: `132537600000000000`; type: Integer.
     */
    private fun formatAllyJoinedWarAggressorMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Ally Has Joined a War", "{ally} has joined {defender} in a war against you. Their participation in the war will start at {[datetime]time}")

    /**
     * Fields:
     * - `aggressorID`: `123456`; type: Integer.
     * - `allyID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `startTime`: `132537600000000000`; type: Integer.
     */
    private fun formatAllyJoinedWarAllyMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Ally Has Joined a War", "{ally} has joined {defender} in a war against {aggressor}. Their participation in the war will start at {[datetime]time}")

    /**
     * Fields:
     * - `aggressorID`: `123456`; type: Integer.
     * - `allyID`: `123456`; type: Integer.
     * - `startTime`: `132537600000000000`; type: Integer.
     */
    private fun formatAllyJoinedWarDefenderMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Ally Has Joined a War", "{ally} has joined your war against {aggressor} and can start fighting by your side at {[datetime]time}")

    /**
     * Fields:
     * - `corporationID`: `123456`; type: Integer.
     * - `standingsChange`: `0.0`; type: Float.
     * - `hours`: `example`; type: String.
     */
    private fun formatBattlePunishFriendlyFire(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Standings Loss from Friendly Fire", "While we appreciate your effort in fighting on behalf of {corporationName}, sustained friendly fire will not be tolerated. As such we have reduced your standings with us by {standingsChange} and you will be unable to participate in any faction contracts for {hours} hours.")

    /**
     * Fields:
     * - `billTypeID`: `123456`; type: Integer.
     * - `dueDate`: `132537600000000000`; type: Integer.
     */
    private fun formatBillOutOfMoneyMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Insufficient Funds for Bill", "The selected corporation wallet division for autopayments does not have enough current funds available to pay the {billType} due to be paid by {[datetime]dueDate, date=full, time=short}. Transfer additional funds to the selected wallet division in order to meet your pending automatic bills.")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `dueDate`: `132537600000000000`; type: Integer.
     */
    private fun formatBillPaidCorpAllMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Bill paid", "This is an automatic message. A debt of {[numeric]amount.isk} owed to you by the sender of this message, due {[datetime]dueDate, date=full, time=none} has been paid {[datetime]notification_created, date=full, time=none}.")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `charID`: `123456`; type: Integer.
     */
    private fun formatBountyClaimMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Bounty payment", "For your termination of {[character]charID.name, linkify} we have paid you {[numeric]amount.isk} from their bounty pool.")

    /**
     * Fields:
     * - `bounty`: `0.0`; type: Float.
     * - `bountyPlacerID`: `123456`; type: Integer.
     */
    private fun formatBountyPlacedAlliance(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Bounty placed on your alliance", "{bountyPlacer} placed {amount} in bounty on your alliance.")

    /**
     * Fields:
     * - `bounty`: `0.0`; type: Float.
     * - `bountyPlacerID`: `123456`; type: Integer.
     */
    private fun formatBountyPlacedChar(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Bounty placed on you", "{bountyPlacer} placed {amount} in bounty on your head.")

    /**
     * Fields:
     * - `bounty`: `0.0`; type: Float.
     * - `bountyPlacerID`: `123456`; type: Integer.
     */
    private fun formatBountyPlacedCorp(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Bounty placed on your corporation", "{bountyPlacer} placed {amount} in bounty on your corporation.")

    /**
     * Fields:
     * - `bounty`: `0.0`; type: Float.
     * - `victimID`: `123456`; type: Integer.
     */
    private fun formatBountyYourBountyClaimed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "A Bounty on {victim} has Been Claimed", "{victim} has been killed and a bounty of the sum of {bountyPaid} been claimed.")

    /**
     * Fields:
     * - `level`: `0.0`; type: Float.
     * - `message`: `example`; type: String.
     */
    private fun formatBuddyConnectContactAdd(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatContact(data + ("messageText" to "Connected through Buddy Connect"), typeDetails, "You have been added as a contact", "{[character]notification_senderID.name, linkify} has added you as a contact with {level}.<br><br>{messageText}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCharAppAcceptMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[character]charID.name, linkify} joins {corporationName}", "Your membership invitation to {[character]charID.name, linkify} has been accepted. <br><br> {applicationText}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCharAppRejectMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[character]charID.name, linkify} rejects invitation", "Your membership invitation to {[character]charID.name, linkify} has been rejected. <br> <br> {applicationText}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCharAppWithdrawMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[character]charID.name, linkify} withdraws application", "{[character]charID.name, linkify} has withdrawn his membership application. <br> <br> {applicationText}")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCharLeftCorpMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[character]charID.name} has left {corporationName}", "{[character]charID.name, linkify} is no longer a member of {corporationName}.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `medalID`: `123456`; type: Integer.
     * - `reason`: `example`; type: String.
     */
    private fun formatCharMedalMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "You have been awarded the {title}", "The {title} decoration has been awarded to you by {issuerCorp} for {reason}. This decoration has the following description: <br><br> {description} <br><br> If you wish to show off this decoration publicly or trash it, you can do so in the decorations tab of your character sheet.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `roleName`: `example`; type: String.
     * - `roleNameIDs`: `[]`; type: Collection.
     * - `security`: `0.0`; type: Float.
     */
    private fun formatCharTerminationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation member {[character]charID.name} has retired", "This is a notification from CONCORD to inform all members of {corpName} that {[character]charID.name, linkify} has retired from active status as a capsuleer as of {[datetime]notification_created, date=full, time=none}. Their callsign is now free for use by future graduates from capsule pilot training who wish to be licensed as independent capsuleers. CONCORD would like to join the members of {corpName} in wishing {[character]charID.name, linkify} all the best in their future endeavors. Sincerely, Duty Officer, Capsuleer Licensing Administration, CONCORD Command, Yulai IX \u2013 CONCORD Bureau")

    /**
     * Fields:
     * - `cloneBought`: `123456`; type: Integer.
     * - `cloneStationID`: `123456`; type: Integer.
     * - `cloneTypeID`: `123456`; type: Integer.
     * - `corpStationID`: `123456`; type: Integer.
     * - `lastCloned`: `123456`; type: Integer.
     * - `podKillerID`: `123456`; type: Integer.
     * - `skillID`: `123456`; type: Integer.
     * - `skillPointsLost`: `123456`; type: Integer.
     */
    private fun formatCloneActivationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Clone activated", "So you died? Well you have a {[item]cloneTypeID.name, linkify} at {[location]corpStationID.name, linkify} station. Enjoy your afterlife. {spLostText}{cloningServiceText}{lastCloneText}{cloneBoughtText}")

    /**
     * Fields:
     * - `cloneStationID`: `123456`; type: Integer.
     * - `corpStationID`: `123456`; type: Integer.
     * - `lastCloned`: `123456`; type: Integer.
     * - `podKillerID`: `123456`; type: Integer.
     */
    private fun formatCloneActivationMsg2(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Clone activated", "Your previous clone was catastrophically terminated. Your consciousness will be transferred to a new clone at {[location]cloneStationID.name, linkify}. {cloningServiceText}\n\nTransfer completed. We wish you good fortune.{lastCloneText}")

    /**
     * Fields:
     * - `charsInCorpID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `newStationID`: `123456`; type: Integer.
     * - `stationID`: `123456`; type: Integer.
     */
    private fun formatCloneMovedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Clone Contract Revoked - {[location]stationID.name}", "This is an automated message. The contracts for medical clones belonging to members of {corporation} have been moved by a station manager of {managerStation} to {[location]newStationID.name, linkify}.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCloneRevokedMsg1(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Clone Contract Revoked - {[location]stationID.name}", "This is an automated message. Your clone contract has been moved by a station manager of {managerStation} to {[location]newStationID.name, linkify}.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `newStationID`: `123456`; type: Integer.
     * - `stationID`: `123456`; type: Integer.
     */
    private fun formatCloneRevokedMsg2(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Clone Contract Revoked - {[location]stationID.name}", "This is an automated message. Your clone contract has been moved by a station manager of {managerStation} to {[location]newStationID.name, linkify}.")

    /**
     * Fields:
     * - `level`: `0.0`; type: Float.
     * - `message`: `example`; type: String.
     */
    private fun formatContactAdd(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatContact(data, typeDetails, "You have been added as a contact", "{[character]notification_senderID.name, linkify} has added you as a contact with {level}.<br><br>{messageText}")

    /**
     * Fields:
     * - `level`: `0.0`; type: Float.
     * - `message`: `example`; type: String.
     */
    private fun formatContactEdit(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatContact(data, typeDetails, "Your contact level has been modified", "{[character]notification_senderID.name, linkify} has changed your contact level to {level}.<br><br>{messageText}")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `password`: `example`; type: String.
     * - `passwordType`: `example`; type: String.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `stationID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     */
    private fun formatContainerPasswordMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Authentication code for {[character]charID.name}", "This is an automated message. The {passwordType, lowercase} password for the {[item]typeID.name, linkify} in {locationName} is: {password}")

    private fun formatContractRegionChangedToPochven(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Contract Region Changed", "The creation of the Triglavian region Pochven has changed details in some of your contract/contracts. Please review them through (My Contracts).")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `billTypeID`: `123456`; type: Integer.
     * - `creditorID`: `123456`; type: Integer.
     * - `currentDate`: `132537600000000000`; type: Integer.
     * - `debtorID`: `123456`; type: Integer.
     * - `dueDate`: `132537600000000000`; type: Integer.
     * - `externalID`: `123456`; type: Integer.
     * - `externalID2`: `123456`; type: Integer.
     */
    private fun formatCorpAllBillMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val formattedData = data + mapOf(
            "currentDate" to (data["currentDate"] ?: data["notification_created"]),
            "allianceID" to data["externalID"],
        )
        val body = when ((data["billTypeID"] as? Number)?.toInt()) {
            1 -> "This is an automatic message. A bill of {[numeric]amount.isk}, due {[datetime]dueDate, date=full, time=none} owed by you to {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This fine is for attempting a purchase that you could not afford. Namely {[item]externalID.name, linkify} at {[location]externalID2.name, linkify}."
            2 -> "This is an automatic message. A bill of {[numeric]amount.isk}, due {[datetime]dueDate, date=full, time=none} owed by you to {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This bill is for extending the lease on your {[item]externalID.name, lowercase} at {[location]externalID2.name, linkify}."
            3 -> "This is an automatic message. A bill of {[numeric]amount.isk} ISK, due {[datetime]dueDate, date=full, time=none} owed by you to your broker {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This bill is for services rendered, in relation to broker order: {externalID} at {[location]externalID2.name, linkify}."
            4 -> "This is an automatic message. A bill of {[numeric]amount.isk}, due {[datetime]dueDate, date=full, time=none} owed by you to {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This bill is for continuing the war against {against}."
            5 -> "This is an automatic message. A bill of {[numeric]amount.isk}, due {[datetime]dueDate, date=full, time=none} owed by {allianceName} to {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This bill is for the maintenance of {allianceName}."
            6 -> "This is an automatic message. A bill of {[numeric]amount.isk}, due {[datetime]dueDate, date=full, time=none} owed by {debtorsName} to {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This bill is for the upkeep of {[location]externalID2.name, linkify} system."
            7 -> "This is an automatic message. A bill of {[numeric]amount.isk}, due {[datetime]dueDate, date=full, time=none} owed by {debtorsName} to {creditorsName} was issued {[datetime]currentDate, date=full, time=none}. This bill is for the upkeep of the Infrastructure Hub in {[location]externalID2.name, linkify} system."
            else -> return formatGeneric(data)
        }
        return formatDocumented(formattedData, typeDetails, "Bill issued", body)
    }

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpAppAcceptMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Invited to join {corporationName}", "Dear {[character]charID.name}, Your application to <b>{corporationName}</b> has been reviewed and an invitation to join the corporation has been sent to you. You may now choose to Accept the invitation which will move you to the corporation or alternatively you may Withdraw the application. The invitation can be found in the Corporation window under the Recruitment \u201cMy Applications\u201d tab. Regards, {corporationName}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `invokingCharID`: `123456`; type: Integer.
     */
    private fun formatCorpAppInvitedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Invited to join {corporationName}", "Dear {[character]charID.name}, My name is {[character]invokingCharID.name} and I would like you to join my corporation <b>{corporationName}</b>. You may choose to Accept the invitation which will move you to the corporation or alternatively you may Decline the invitation. The invitation can be found in the Corporation window under the Recruitment \u201cMy Applications\u201d tab. Regards, {[character]invokingCharID.name} {corporationName}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpAppNewMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "New application from {[character]charID.name, linkify}", "{applicationText}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `customMessage`: `example`; type: String.
     */
    private fun formatCorpAppRejectCustomMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Rejected application to join {corporationName}", "Dear {[character]charID.name}, your application to join <b>{corporationName}</b> has been processed and rejected. {customMessage} Regards, {corporationName}")

    /**
     * Fields:
     * - `applicationText`: `example`; type: String.
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpAppRejectMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Rejected application to join {corporationName}", "Dear {[character]charID.name}, your application to join <b>{corporationName}</b> has been processed and rejected. Regards, {corporationName}")

    private fun formatCorpBecameWarEligible(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your corporation or alliance is now eligible for formal war declarations", "Your corporation or alliance is now eligible participate in formal war declarations.<br>This could be because your corporation and/or one of the corporations in your alliance owns a structure deployed in space.")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `corpID`: `123456`; type: Integer.
     * - `isMembers`: `false`; type: Boolean.
     * - `payout`: `0.0`; type: Float.
     */
    private fun formatCorpDividendMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Dividend from {corporationName}", "{body}")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpFriendlyFireDisableTimerCompleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation friendly-fire is now an illegal act", "The \u2018friendly-fire\u2019 aggression policy within {corporationName} has now changed. Aggressive actions between corporation members are now considered an illegal action, subject to standard CONCORD penalty protocols. Corporation members will now receive Criminal or Suspect flags for attacking others in Empire-controlled space.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `timeFinished`: `132537600000000000`; type: Integer.
     */
    private fun formatCorpFriendlyFireDisableTimerStarted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation friendly-fire is becoming an illegal act", "{characterName} is changing the \u2018friendly-fire\u2019 aggression policy within {corporationName}. After 24 hours, aggressive actions between corporation members will be considered an illegal action, subject to standard CONCORD penalty protocols. The new rules will come in to effect at {[datetime]dueDate, date=full, time=short}, after which corporation members will receive Criminal or Suspect flags for attacking others in Empire-controlled space.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpFriendlyFireEnableTimerCompleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation friendly-fire is now a legal act", "The \u2018friendly-fire\u2019 aggression policy within {corporationName} has now changed. Aggressive actions between corporation members are now considered as legal, and as such will not be subject to any CONCORD penalties. Corporation members may now freely attack each other.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `timeFinished`: `132537600000000000`; type: Integer.
     */
    private fun formatCorpFriendlyFireEnableTimerStarted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation friendly-fire is becoming a legal act", "{characterName} is changing the \u2018friendly-fire\u2019 aggression policy within {corporationName}. After 24 hours, aggressive actions between corporation members will be considered as legal, and as such will not be subject to any CONCORD penalties. The new rules will come in to effect at {[datetime]dueDate, date=full, time=short}, after which corporation members may freely attack each other.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpKicked(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Kicked from {corporationName}", "This is an automated message from {corporationName} hereby informing you that your services will no longer be required and that your membership of the corporation has been terminated with immediate effect. Any corporation project rewards you have earned but not yet claimed will automatically be received within 30 days of your last contribution to that project. Thank you for your time with {corporationName} and the best of luck with your future endeavors.")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `corpID`: `123456`; type: Integer.
     * - `payout`: `0.0`; type: Float.
     */
    private fun formatCorpLiquidationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Liquidation Settlement from {corporationName}", "{corporationName} may have credited your account as part of a total payout of {[numeric]payout.isk} from funds generated from the liquidation of the corporation. The amount awarded is based upon the number of shares you hold, in relation to the total number of shares issued by the corporation.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `newCeoID`: `123456`; type: Integer.
     * - `oldCeoID`: `123456`; type: Integer.
     */
    private fun formatCorpNewCEOMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[character]newCeoID.name} is the new CEO of {corporationName}", "{[character]oldCeoID.name, linkify} has resigned from the position of CEO with {corporationName}. As their last move they appointed {[character]newCeoID.name, linkify} as CEO.")

    /**
     * Fields:
     * - `body`: `example`; type: String.
     * - `corpID`: `123456`; type: Integer.
     * - `inEffect`: `123456`; type: Integer.
     * - `parameter`: `123456`; type: Integer.
     * - `voteType`: `123456`; type: Integer.
     */
    private fun formatCorpNewsMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val subject = when ((data["voteType"] as? Number)?.toInt()) {
            1 -> if (data["inEffect"].isTruthy()) {
                "{corpName} declares war on {parameterCorpName}"
            } else {
                "{corpName} is no longer at war with {parameterCorpName}"
            }
            2 -> "{corpName} creates {parameter} shares"
            3 -> "{corpName} expels {[character]parameter.name, linkify}"
            else -> ""
        }
        val body = (data["body"] as? String).orEmpty().ifEmpty { subject }
        return formatDocumented(data, typeDetails, subject, body)
    }

    private fun formatCorpNoLongerWarEligible(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your corporation or alliance is no longer eligible for formal war declarations", "Your corporation or alliance is no longer eligible to participate in formal war declarations.<br>Neither your corporation nor any of the corporations in your alliance own a structure deployed in space at this time. If your corporation or alliance is currently involved in a formal war, that war will end in 24 hours.")

    /**
     * Fields:
     * - `typeID`: `123456`; type: Integer.
     * - `errorText`: `example`; type: String.
     */
    private fun formatCorpOfficeExpirationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Lease Expiration Notice", "{whereText} You have {rentPeriod} days to vacate the location. If the location is not empty after that period then your belongings there will be taken as payment. {reasonText}")

    private fun formatCorpStructLostMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: Sovereignty hub has been lost", "<br>Contact with the sovereignty hub in system {[location]solarSystemID.name, linkify} has been lost. The structure has presumably been destroyed.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `newTaxRate`: `0.0`; type: Float.
     * - `oldTaxRate`: `0.0`; type: Float.
     */
    private fun formatCorpTaxChangeMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{currencyName} Tax Rate Changed To {newTaxRate}%", "This is an automated message: The {currencyName} tax rate for {corporationName} members has been changed from {oldTaxRate}% to {newTaxRate}%.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatCorpVoteCEORevokedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "CEO roles revoked", "{[character]charID.name, linkify} is running for CEO in {corporationName}. Your roles as CEO have been revoked for the duration of the voting period.")

    /**
     * Fields:
     * - `body`: `example`; type: String.
     * - `subject`: `example`; type: String.
     */
    private fun formatCorpVoteMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporate Vote - {subject}", "{body}")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     */
    private fun formatCorpWarDeclaredMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{declaredByName} Declares War Against {againstName}", "{declaredByName} has declared war on {againstName}.<br>Within 24 hours fighting can legally occur between those involved. If war is due to a corporation at war joining or leaving an alliance, then the war starts immediately instead.<br><br>{costText}")

    private fun formatCorpWarDeclaredV2(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatWarDeclared(data, typeDetails)

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     */
    private fun formatCorpWarFightingLegalMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "War between {declaredByName} and {againstName} has started", "{declaredByName} has declared war on {againstName}.<br>Fighting can legally occur between those involved.<br><br>{costText}")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     */
    private fun formatCorpWarInvalidatedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "CONCORD invalidates war declared by {declaredByName} against {againstName}", "CONCORD has declared this war invalid as it breaches one or more articles in the Yulai Convention. The war will be declared as being over after approximately 24 hours.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     */
    private fun formatCorpWarRetractedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{declaredByName} Retracts War Against {againstName}", "The war between {declaredByName} and {againstName} is coming to an end. {declaredByName} has retracted the war against {againstName}. The war will be declared as being over after approximately 24 hours.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     */
    private fun formatCorpWarSurrenderMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{againstName} surrenders to {declaredByName}", "The war between {againstName} and {declaredByName} is coming to an end. {againstName} has surrendered to {declaredByName}. The war will be declared as being over after approximately 24 hours and neither organizations will be able to declare new wars against each other for the next 2 weeks.")

    private fun formatCorporationGoalClosed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        return if (data["closer_id"] != null) {
            formatDocumented(data, typeDetails, "Project Cancelled", "Project {goal_name} has been closed by {goal_closer} and will not accept further contributions.")
        } else {
            formatDocumented(data, typeDetails, "Project Cancelled", "Project {goal_name} has been closed and will not accept further contributions.")
        }
    }

    private fun formatCorporationGoalCompleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Project Completed", "Project {goal_name} created by {creator} has been successfully completed after reaching its target.")

    private fun formatCorporationGoalCreated(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "New Project Available", "Project {goal_name} has been created by {creator} and is open for contributions.")

    private fun formatCorporationGoalExpired(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Project Expired", "Project {goal_name} created by {creator} has expired and will not accept further contributions.")

    private fun formatCorporationGoalLimitReached(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Project's Contribution Limit Reached", "You have reached your contribution limit for the project {goal_name} created by {creator}, and you will not be able to make any further contributions to it.")

    private fun formatCorporationGoalNameChange(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Project Renamed", "One of the corporation projects has been renamed to {goal_name}")

    private fun formatCorporationLeft(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Quit {corp_name}", "This notification is an acknowledgment by {corp_name} of your recent decision to quit the {corp_name} corporation.<br><br>Any corporation project rewards you have earned but not yet claimed will automatically be received within 30 days of your last contribution to that project.<br><br>Good luck with all your future endeavors.")

    /**
     * Fields:
     * - `factionID`: `123456`; type: Integer.
     * - `fine`: `0.0`; type: Float.
     * - `penalty`: `0.0`; type: Float.
     * - `quantity`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `lostList`: `[]`; type: Collection.
     * - `securityLevel`: `0.0`; type: Float.
     * - `shouldAttack`: `false`; type: Boolean.
     * - `shouldConfiscate`: `false`; type: Boolean.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `standingDivision`: `0.0`; type: Float.
     */
    private fun formatCustomsMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Contraband Confiscation", "<br>Empire: {factionName}<br>Solar System: {[location]solarSystemID.name, linkify}<br>System Security Level: {securityLevel}<br><br>Found Contraband:<br><br>{confiscatedItems}Total Standing Penalties (ideally): {[numeric]ideal, decimalPlaces=2}<br>Total Standing Penalties (actual): {[numeric]actual, decimalPlaces=2}<br>Total Fines: {[numeric]total.isk}<br>{summaryResponseText}")

    /**
     * Fields:
     * - `station_id`: `123456`; type: Integer.
     * - `type_id`: `123456`; type: Integer.
     */
    private fun formatDailyItemRewardAutoClaimed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Daily Reward Auto-Redeemed", "Your Daily Goal reward {[item]item_type_id.name, linkify} has been automatically redeemed to {[location]station_id.name, linkify} after being unclaimed for 30 days.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `entityID`: `123456`; type: Integer.
     */
    private fun formatDeclareWar(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{entityName} declared war", "{charName} has declared war on {defenderName} on behalf of {entityName}.")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `celestialIndex`: `example`; type: String.
     * - `districtIndex`: `example`; type: String.
     * - `startDate`: `132537600000000000`; type: Integer.
     * - `attackerID`: `123456`; type: Integer.
     */
    private fun formatDistrictAttacked(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your district {DistrictName} has been attacked", "A district belonging to your corporation has been attacked: District: {DistrictName} Time of battle: {BattleTime} Attacking corporation: {AttackingCorporation}")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatDustAppAcceptedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Welcome to {corporationName}", "Dear {[character]charID.name}, your application to join <b>{corporationName}</b> has been processed and accepted. {applicationText} Regards, {corporationName}")

    private fun formatESSMainBankLink(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "ESS Main Bank Intrusion", "A pilot has linked to the ESS Main Bank in {[location]solarSystemID.name, linkify} and a security intrusion is in progress.")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatEntosisCaptureStarted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} is being captured", "A capsuleer has started to influence this structure with an Entosis Link. No further Entosis alerts for this structure will be sent until 60 minutes has passed or the structure has been secured by your alliance.")

    private fun formatExpertSystemExpired(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Expert System Expired", "Your access to {[item]typeID.name, linkify} has expired. The skill levels it provided are no longer available to you, unless you have them trained or they are provided by another Expert System you have an access to.")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFWAllianceKickMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{allianceName} expelled from {factionName}", "{allianceName} has been expelled from the {factionName} faction because the standing of corporation(s) within the alliance is less than the minimum required standing of {[numeric]requiredStanding, decimalPlaces=2}. The corporations are: {corpList}")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `corpList`: `[]`; type: Collection.
     * - `factionID`: `123456`; type: Integer.
     * - `requiredStanding`: `0.0`; type: Float.
     */
    private fun formatFWAllianceWarningMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Low standing warning", "The standings from some corporation(s) towards the {factionName} in this alliance have dropped below the minimum of {[numeric]requiredStanding, decimalPlaces=2}. If the standing has not improved within 24 hours, then {allianceName} will be expelled from the {factionName} faction. The corporations are: {corpList}")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFWCharKickMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Expelled from {corporationName}", "You have been expelled from the {corporationName} because your standing of {[numeric]currentStanding, decimalPlaces=4} towards the {factionName} faction is below the {[numeric]requiredStanding, decimalPlaces=2} minimum")

    /**
     * Fields:
     * - `factionID`: `123456`; type: Integer.
     * - `newRank`: `123456`; type: Integer.
     */
    private fun formatFWCharRankGainMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(
        data,
        typeDetails,
        "Faction Warfare promotion",
        "You have been promoted to rank {[numeric]newRank, useGrouping} in the {factionName} faction.",
    )

    /**
     * Fields:
     * - `factionID`: `123456`; type: Integer.
     * - `newRank`: `123456`; type: Integer.
     */
    private fun formatFWCharRankLossMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(
        data,
        typeDetails,
        "Faction Warfare demotion",
        "You have been demoted to rank {[numeric]newRank, useGrouping} in the {factionName} faction.",
    )

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFWCharWarningMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Low standing warning", "Your standing towards the {factionName} faction has dropped to {[numeric]currentStanding, decimalPlaces=4} which is below the minimum of {[numeric]requiredStanding, decimalPlaces=2}. If the standing has not improved within 24 hours, you will be expelled from the {corporationName}.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFWCorpJoinMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} has joined {factionName}", "{corporationName} has joined the {factionName} faction at war. Fighting may begin.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `currentStanding`: `0.0`; type: Float.
     * - `factionID`: `123456`; type: Integer.
     * - `requiredStanding`: `0.0`; type: Float.
     */
    private fun formatFWCorpKickMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} expelled from {factionName}", "{corporationName} has been expelled from the {factionName} faction because the standing of {[numeric]currentStanding, decimalPlaces=4} is less than the minimum required standing of {[numeric]requiredStanding, decimalPlaces=2}")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFWCorpLeaveMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} has left {factionName}", "{corporationName} has left the {factionName} faction and may no longer legally fight on its behalf.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `currentStanding`: `0.0`; type: Float.
     * - `factionID`: `123456`; type: Integer.
     * - `requiredStanding`: `0.0`; type: Float.
     */
    private fun formatFWCorpWarningMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Low standing warning", "{corporationName}'s standing towards the {factionName} faction has dropped to {[numeric]currentStanding, decimalPlaces=4} which is below the minimum of {[numeric]requiredStanding, decimalPlaces=2}. If the standing has not improved within 24 hours, {corporationName} will be expelled from the {factionName} faction.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFacWarCorpJoinRequestMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} is joining {factionName}", "{corporationName} is joining the {factionName} faction by enlisting into Factional Warfare. Fighting may begin within 24 hours. At that time, {corporationName} will be at war with all enemies of the {factionName} faction.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFacWarCorpJoinWithdrawMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} has withdrawn request to join {factionName}", "{corporationName} has withdrawn its request to join the {factionName} faction at war.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFacWarCorpLeaveRequestMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} is leaving {factionName}", "{corporationName} is leaving the {factionName} faction within 24 hours. After that time {corporationName} may no longer fight on behalf of {factionName}.")

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     * - `factionID`: `123456`; type: Integer.
     */
    private fun formatFacWarCorpLeaveWithdrawMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{corporationName} has withdrawn request to leave {factionName}", "{corporationName} has withdrawn its request to leave the {factionName} faction.")

    /**
     * Fields:
     * - `factionID`: `123456`; type: Integer.
     * - `corporationID`: `123456`; type: Integer.
     * - `rank`: `example`; type: String.
     * - `requiredRank`: `example`; type: String.
     */
    private fun formatFacWarDirectEnlistmentRevoked(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val body = if (data["corporationID"] != null) {
            "Your direct enlistment in {faction} was revoked due to: {corporation} is blocked from enlisting with {faction}."
        } else {
            val rank = (data["rank"] as? Number)?.toDouble()?.times(10)
            val requiredRank = (data["requiredRank"] as? Number)?.toDouble()?.times(10)
            return formatDocumented(
                data + mapOf("standing" to rank, "required" to requiredRank),
                typeDetails,
                "Direct Enlistment Revoked",
                "Your direct enlistment in {faction} was revoked due to: Your current {faction} standing {[numeric]standing, decimalPlaces=4} is below the minimum of {[numeric]required, decimalPlaces=2}.",
            )
        }
        return formatDocumented(data, typeDetails, "Direct Enlistment Revoked", body)
    }

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `charRefID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `disqualificationType`: `123456`; type: Integer.
     * - `event`: `123456`; type: Integer.
     * - `itemRefID`: `123456`; type: Integer.
     * - `locationID`: `123456`; type: Integer.
     */
    private fun formatFacWarLPDisqualifiedEvent(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val reason = when ((data["disqualificationType"] as? Number)?.toInt()) {
            2 -> "You were not flying a type of ship that contributes to fighting in a meaningful way."
            3 -> "Your ship was cloaked at the time."
            9 -> "You were too far away from the target."
            12 -> "You did not contribute enough."
            else -> "Unknown"
        }
        val body = when ((data["event"] as? Number)?.toInt()) {
            363 -> "Although you were present at the capture of {location}, the following circumstances prevent you from qualifying for loyalty points for your effort:<br>{reason}"
            359 -> "Although you were present at the capture of enemy site in {location}, the following circumstances prevent you from qualifying for loyalty points for your effort:<br>{reason}"
            373 -> "Although you were attempting to defend {location}, the following circumstances prevent you from qualifying for loyalty points for your effort:<br>{reason}"
            else -> "Although you were present at the capture of the enemy site in {location}, you did not receive any loyalty points for unknown reasons."
        }
        return formatDocumented(data + ("reason" to reason), typeDetails, "Not qualified for loyalty points in militia effort", body)
    }

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `charRefID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `disqualificationType`: `123456`; type: Integer.
     * - `event`: `123456`; type: Integer.
     * - `itemRefID`: `123456`; type: Integer.
     * - `locationID`: `123456`; type: Integer.
     */
    private fun formatFacWarLPDisqualifiedKill(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Not qualified for loyalty points in militia effort", "While we appreciate your effort to shoot down {victim}, a despised enemy of our militia, {corporation} is unable to grant you any loyalty points since the value of the lost ship is less than its insurance value.")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `charRefID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `event`: `123456`; type: Integer.
     * - `itemRefID`: `123456`; type: Integer.
     * - `locationID`: `123456`; type: Integer.
     */
    private fun formatFacWarLPPayoutEvent(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val amount = (data["amount"] as? Number)?.toDouble() ?: 0.0
        val subject = if (amount > 0) "Loyalty points awarded for militia effort" else "No loyalty points awarded for militia effort"
        val body = when ((data["event"] as? Number)?.toInt()) {
            363 -> "In recognition of your effort towards the successful capture of {location}, {corporation} grants you {[numeric]amount, useGrouping, decimalPlaces=0} loyalty points."
            359 -> if (amount > 0) {
                "In recognition of your effort to capture an enemy site in {location}, {corporation} grants you {[numeric]amount, useGrouping, decimalPlaces=0} loyalty points."
            } else {
                "You have secured an enemy site in {location}. However, because the system was already 100% vulnerable the {corporation} cannot authorize a transfer of loyalty points at this time."
            }
            373 -> if (amount > 0) {
                "In recognition of your effort to secure our strategic facility in the contested system of {location}, {corporation} grants you {[numeric]amount, useGrouping, decimalPlaces=0} loyalty points."
            } else {
                "You have secured our facility in {location}. However, because the system is not contested by the enemy the {corporation} cannot authorize a transfer of loyalty points at this time."
            }
            371 -> return formatGeneric(data)
            else -> "{corporation} has granted you {[numeric]amount, useGrouping, decimalPlaces=0} loyalty points for your efforts towards the cause of your militia."
        }
        return formatDocumented(data, typeDetails, subject, body)
    }

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `charRefID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `event`: `123456`; type: Integer.
     * - `itemRefID`: `123456`; type: Integer.
     * - `locationID`: `123456`; type: Integer.
     */
    private fun formatFacWarLPPayoutKill(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Loyalty points awarded for militia effort", "In recognition of your effort to shoot down {victim}, a despised enemy of our militia, {corporation} grants you {[numeric]amount, useGrouping, decimalPlaces=0} loyalty points which will be delivered to you within 15 minutes.")

    /**
     * Fields:
     * - `project_id`: `123456`; type: Integer.
     * - `project_name`: `example`; type: String.
     * - `creator_id`: `123456`; type: Integer.
     * - `corporation_id`: `123456`; type: Integer.
     */
    private fun formatFreelanceProjectACLDeleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Freelance Job's Active ACL has been Deleted", "The ACL associated with the Freelance Job {project_name} created by {job_creator_name} on behalf of {issuing_corporation_name} has been deleted. Participants that have already accepted will be able to continue contributing to the job, but no new participant can accept it.")

    private fun formatFreelanceProjectClosed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val closer = when {
            data["admin_closer"].isTruthy() -> "Administrator"
            data["player_closer"] != null -> "{closing_character_name}"
            else -> "Unknown"
        }
        return formatDocumented(data, typeDetails, "Freelance Job Cancelled", "Freelance Job {project_name} issued by {job_creator_name} has been closed by $closer and will not accept further contributions.")
    }

    /**
     * Fields:
     * - `project_id`: `123456`; type: Integer.
     * - `project_name`: `example`; type: String.
     * - `creator_id`: `123456`; type: Integer.
     * - `corporation_id`: `123456`; type: Integer.
     */
    private fun formatFreelanceProjectCompleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Freelance Job Completed", "Freelance Job {project_name} created by {job_creator_name} on behalf of {issuing_corporation_name} has been successfully completed after reaching its target.")

    /**
     * Fields:
     * - `project_id`: `123456`; type: Integer.
     * - `project_name`: `example`; type: String.
     * - `creator_id`: `123456`; type: Integer.
     * - `corporation_id`: `123456`; type: Integer.
     */
    private fun formatFreelanceProjectCreated(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Freelance Job Created", "Freelance Job {project_name} was created by {job_creator_name} on behalf of {issuing_corporation_name}. This job is now accepting contributions.")

    /**
     * Fields:
     * - `project_id`: `123456`; type: Integer.
     * - `project_name`: `example`; type: String.
     * - `creator_id`: `123456`; type: Integer.
     * - `corporation_id`: `123456`; type: Integer.
     */
    private fun formatFreelanceProjectExpired(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Freelance Job Expired", "Freelance Job {project_name} created by {job_creator_name} on behalf of {issuing_corporation_name} has expired and will not accept further contributions.")

    /**
     * Fields:
     * - `project_id`: `123456`; type: Integer.
     * - `project_name`: `example`; type: String.
     * - `creator_id`: `123456`; type: Integer.
     * - `corporation_id`: `123456`; type: Integer.
     */
    private fun formatFreelanceProjectLimitReached(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Freelance Job's Contribution Limit Reached", "You have reached your contribution limit for the Freelance Job {project_name} created by {job_creator_name} on behalf of {issuing_corporation_name}. You will not be able to make any further contributions to it.")

    /**
     * Fields:
     * - `project_id`: `123456`; type: Integer.
     * - `project_name`: `example`; type: String.
     * - `kicker`: `example`; type: String.
     * - `corporation_id`: `123456`; type: Integer.
     */
    private fun formatFreelanceProjectParticipantKicked(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val kickerId = when (val kicker = data["kicker"]) {
            is Number -> kicker.toLong()
            is String -> kicker.toLongOrNull()
            else -> null
        }
        val body = if (kickerId?.let { typeDetails[it] } is TypeDetail.Character) {
            "You were removed from {project_name} by {kicking_character_name} on behalf of {issuing_corporation_name}. You will no longer be able to contribute to this job. All of your outstanding rewards have been auto-redeemed."
        } else {
            "You were removed from {project_name} on behalf of {issuing_corporation_name}. You will no longer be able to contribute to this job. All of your outstanding rewards have been auto-redeemed."
        }
        return formatDocumented(data, typeDetails, "Removed from Freelance Job", body)
    }

    private fun formatGameTimeAdded(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Omega Time", "Clone State Omega time added to your account")

    /**
     * Fields:
     * - `message`: `example`; type: String.
     * - `offerID`: `123456`; type: Integer.
     * - `quantity`: `123456`; type: Integer.
     * - `senderCharID`: `123456`; type: Integer.
     */
    private fun formatGameTimeReceived(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Plex Donation", "{charactername} has donated {quantity} days of Clone State Omega time to you.")

    /**
     * Fields:
     * - `receiverCharID`: `123456`; type: Integer.
     * - `senderCharID`: `123456`; type: Integer.
     */
    private fun formatGameTimeSent(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Plex Donation", "You have donated 30 days of Clone State Omega time to {charactername}")

    /**
     * Fields:
     * - `message`: `example`; type: String.
     * - `offerID`: `123456`; type: Integer.
     * - `quantity`: `123456`; type: Integer.
     * - `senderCharID`: `123456`; type: Integer.
     */
    private fun formatGiftReceived(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "You have a gift!", "You have received a gift!")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatIHubDestroyedByBillFailure(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has self-destructed due to unpaid maintenance bills", "This Infrastructure Hub has self-destructed, as the standard maintenance bills were not paid.")

    /**
     * Fields:
     * - `emailMessageId`: `example`; type: String.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `taleID`: `123456`; type: Integer.
     * - `topTen`: `[]`; type: Collection.
     */
    private fun formatIncursionCompletedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Liberation of {[location]constellationID.name} complete", "{[character]notification_receiverID.name, linkify}<br> <br> {[location]constellationID.name, linkify} has been freed from the invading Sansha forces. Your efforts in this matter have not gone unnoticed and we are awarding you loyalty points. More details can be found in the {journalLink}.<br> <br> In addition, we wish to honor the following pilots, whose individual contribution exceeded everyone else:<br> <br> {topTenString} <br> CONCORD")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `systemBids`: `[]`; type: Collection.
     * - `teamNameInfo`: `[]`; type: Collection.
     * - `totalIsk`: `0.0`; type: Float.
     * - `yourAmount`: `0.0`; type: Float.
     */
    private fun formatIndustryTeamAuctionLost(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Team Auction Lost", "Your bid on {teamName} was not successful. {location} had the highest bid of {totalAmount}. Your bid of {amount} will be returned to you. {teamName} is now available for hire in {location}.<br><br>The top {[numeric]numBids} contributors were<br>{bids}")

    private fun formatIndustryTeamAuctionWon(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Team Auction Won", "You contributed to the winning bid for {teamName} on behalf of {location} The winning bid was {totalAmount} and your contribution was {amount} ISK. {teamName} is now available for hire in {location}.<br><br>The top {[numeric]numBids} contributors were<br>{bids}")

    /**
     * Fields:
     * - `billID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `dueDate`: `132537600000000000`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatInfrastructureHubBillAboutToExpire(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "IHub Bill About to Expire", "Maintainance bill for Infastructure hub in {[location]solarSystemID.name, linkify} expires at {[datetime]dueDate, date=full, time=short}, if not paid in time this Infrastructure Hub will self-destruct")

    /**
     * Fields:
     * - `endDate`: `132537600000000000`; type: Integer.
     * - `shipID`: `123456`; type: Integer.
     * - `shipName`: `example`; type: String.
     * - `startDate`: `132537600000000000`; type: Integer.
     */
    private fun formatInsuranceExpirationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Insurance Contract Expired", "Dear valued customer,<br><br> The insurance contract between yourself and SCC for the insurance of the ship <b>{shipName}</a></b> issued at {[datetime]startDate, date=full, time=short} has expired. Please purchase a new insurance as quickly as possible to protect your investment.<br><br> Best,<br> The Secure Commerce Commission,<br> Reference ID: {shipID}")

    /**
     * Fields:
     * - `isHouseWarmingGift`: `false`; type: Boolean.
     * - `shipTypeID`: `123456`; type: Integer.
     */
    private fun formatInsuranceFirstShipMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Pend Insurance Inc.", "We from the friendly insurance company Pend Insurance regret your temporary lack of spacecraft. According to the agreement between Pend Insurance and SCC, we hereby grant you a brand new {[item]shipTypeID.name, linkify}, fully fitted. Also included is a gratuity gift of {gift}.")

    /**
     * Fields:
     * - `endDate`: `132537600000000000`; type: Integer.
     * - `ownerID`: `123456`; type: Integer.
     * - `reason`: `123456`; type: Integer.
     * - `shipID`: `123456`; type: Integer.
     * - `startDate`: `132537600000000000`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     */
    private fun formatInsuranceInvalidatedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Insurance Contract Invalidated", "RefID:{shipID} This is an automated message. The contract for ship insurance you made for a {[item]typeID.name, linkify} from {[datetime]startDate, date=full, time=short} to {[datetime]endDate, date=full, time=short} is considered invalid because {reason}. Due to this, no payment is being made for the reported destruction of this ship.")

    /**
     * Fields:
     * - `endDate`: `132537600000000000`; type: Integer.
     * - `itemID`: `123456`; type: Integer.
     * - `level`: `0.0`; type: Float.
     * - `numWeeks`: `123456`; type: Integer.
     * - `shipName`: `example`; type: String.
     * - `startDate`: `132537600000000000`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     */
    private fun formatInsuranceIssuedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Insurance Contract Issued", "Dear valued customer,<br><br> Congratulations on the insurance on your ship. A very wise choice indeed.<br> This letter is to confirm that we have issued an insurance contract for your ship, <a href=\"showinfo:{typeID}//{itemID}\">{shipName}</a> ({[item]typeID2.name, linkify}) at a level of {level}%.<br> This contract will expire at {[datetime]endDate, date=full, time=short}, after {numWeeks} weeks. <br><br> Best,<br> The Secure Commerce Commission,<br> Reference ID: {itemID}")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     * - `itemID`: `123456`; type: Integer.
     * - `payout`: `false`; type: Boolean.
     */
    private fun formatInsurancePayoutMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Insurance", "RefID: {itemID} Your friendly insurance company has transferred {[numeric]amount.isk} into your account for the recent loss of your ship. {defaultPayoutText}")

    /**
     * Fields:
     * - `topTen`: `example`; type: String.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatInvasionCompletedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Triglavians driven out of {[location]solarSystemID.name}", "{[character]notification_receiverID.name, linkify}<br> <br> The defense of {[location]solarSystemID.name, linkify} and the surrounding systems has been a success! The area has been cleared of invading Triglavian forces. The Directive Enforcement Department of CONCORD and all system inhabitants are grateful for your efforts in defending New Eden from the attackers; you have been awarded loyalty points and ISK for your assistance.<br> <br> In addition, we wish to honor the following pilots, whose individual contribution exceeded everyone else:<br> <br> {topTenString} <br> CONCORD")

    private fun formatInvasionSystemLogin(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Triglavian Invasion", "A Triglavian Invasion is underway in your vicinity! Join the fight and help drive the invaders out, or relocate immediately if you are unprepared for battle. Any technology or equipment left by the invaders will be yours to loot.<br>\n\nCONCORD")

    private fun formatInvasionSystemStart(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Triglavian Vessels Spotted", "Triglavian activity has been reported in your area. You will be awarded with ISK and loyalty points courtesy of the Directive Enforcement Department of CONCORD if you successfully join the fight and drive the invaders out! Should you be unprepared for battle, exercise caution in relocating.<br>\n\nCONCORD")

    /**
     * Fields:
     * - `locationID`: `123456`; type: Integer.
     * - `locationOwnerID`: `123456`; type: Integer.
     * - `ownerID`: `123456`; type: Integer.
     * - `typeIDs`: `[]`; type: Collection.
     */
    private fun formatJumpCloneDeletedMsg1(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Jump clone destruction", "Dear {[character]ownerID.name},\nA jump clone you had installed has been destroyed.\nLocation: {[location]locationID.name, linkify}\nLocation Owner: {[character]locationOwnerID.name, linkify}\nResponsible party for the destruction: {[character]destroyerID.name, linkify}\n{implantListText}")

    /**
     * Fields:
     * - `destroyerID`: `123456`; type: Integer.
     * - `locationID`: `123456`; type: Integer.
     * - `locationOwnerID`: `123456`; type: Integer.
     * - `ownerID`: `123456`; type: Integer.
     * - `typeIDs`: `[]`; type: Collection.
     */
    private fun formatJumpCloneDeletedMsg2(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Jump clone destruction", "Dear {[character]ownerID.name},\nA jump clone you had installed has been destroyed.\nLocation: {[location]locationID.name, linkify}\nLocation Owner: {[character]locationOwnerID.name, linkify}\nResponsible party for the destruction: {[character]destroyerID.name, linkify}\n{implantListText}")

    /**
     * Fields:
     * - `killMailHash`: `example`; type: String.
     * - `killMailID`: `123456`; type: Integer.
     * - `victimID`: `123456`; type: Integer.
     * - `victimShipTypeID`: `123456`; type: Integer.
     */
    private fun formatKillReportFinalBlow(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        return if (data["victimID"] != null) {
            formatDocumented(
                data = data,
                typeDetails = typeDetails,
                subject = "Kill report for {[character]victimID.name} available",
                body = "<a href=\"killReport:{killMailID}:{killMailHash}\">Show killmail</a>"
            )
        } else {
            formatDocumented(
                data = data,
                typeDetails = typeDetails,
                subject = "Kill report for {[item]victimShipTypeID.name, linkify} available",
                body = "<a href=\"killReport:{killMailID}:{killMailHash}\">Show killmail</a>"
            )
        }
    }

    /**
     * Fields:
     * - `killMailHash`: `example`; type: String.
     * - `killMailID`: `123456`; type: Integer.
     * - `victimShipTypeID`: `123456`; type: Integer.
     */
    private fun formatKillReportVictim(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        return formatDocumented(
            data = data,
            typeDetails = typeDetails,
            subject = "Kill report available",
            body = "<a href=\"killReport:{killMailID}:{killMailHash}\">Show killmail</a>"
        )
    }

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `price`: `0.0`; type: Float.
     * - `toEntityID`: `123456`; type: Integer.
     */
    private fun formatKillRightAvailable(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Kill right on you made available", "The kill right {charName} has on you has been made available to {availableToName} for {amount}.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `price`: `0.0`; type: Float.
     */
    private fun formatKillRightAvailableOpen(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Kill right on you made available", "The kill right {charName} has on you has been made available to all for {amount}.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     */
    private fun formatKillRightEarned(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Kill right earned", "As a victim of {charName}\u2018s unlawful aggression you now have a kill right against them. Once activated, the kill right allows you to seek revenge, alone or with friends, without CONCORD intervention.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `toEntityID`: `123456`; type: Integer.
     */
    private fun formatKillRightUnavailable(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Kill right on you made unavailable", "{charName} has made a kill right on you unavailable to {availableToName}.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     */
    private fun formatKillRightUnavailableOpen(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Kill right on you made unavailable", "{charName} has made a kill right on you unavailable to all.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     */
    private fun formatKillRightUsed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your kill right was used", "{charName} has been killed using your kill right. The kill right you had on this capsuleer has now been removed.")

    /**
     * Fields:
     * - `issuer_id`: `123456`; type: Integer.
     * - `amount`: `0.0`; type: Float.
     */
    private fun formatLPAutoRedeemed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val issuerId = data["issuer_id"].asLong()
        return if (issuerId == 1_000_419L) {
            formatDocumented(
                data,
                typeDetails,
                "Completed goal EverMarks reward received",
                "You have been automatically transferred {[numeric]amount, useGrouping, decimalPlaces=0} EverMarks from an unclaimed reward you earned for completing a goal. Goal rewards are automatically transferred after 30 days if not manually claimed earlier.",
            )
        } else {
            val issuerName = issuerId?.let { typeDetails[it]?.getDisplayName() ?: it.toString() } ?: "Unknown"
            formatDocumented(
                data + ("lp_corp_name" to issuerName),
                typeDetails,
                "Completed goal {lp_corp_name} LP reward received",
                "You have been automatically transferred {[numeric]amount, useGrouping, decimalPlaces=0} {lp_corp_name} Loyalty Points from an unclaimed reward you earned for completing a goal. Goal rewards are automatically transferred after 30 days if not manually claimed earlier.",
            )
        }
    }

    /**
     * Fields:
     * - `agentLocation`: `{}`; type: Object/map.
     * - `characterID`: `123456`; type: Integer.
     * - `messageIndex`: `0`; type: Integer.
     * - `targetLocation`: `{}`; type: Object/map.
     */
    private fun formatLocateCharMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val agentLocation = (data.findValue("agentLocation") as? Map<*, *>)?.toLocatorLocation()
        val targetLocation = (data.findValue("targetLocation") as? Map<*, *>)?.toLocatorLocation()
        val locationData = data + mapOf(
            "targetRegionID" to targetLocation?.regionId,
            "targetConstellationID" to targetLocation?.constellationId,
            "targetSolarSystemID" to targetLocation?.solarSystemId,
            "targetStationID" to targetLocation?.stationId,
        )
        val location = when {
            targetLocation?.stationId != null -> "at {[location]targetStationID.name, linkify}"
            targetLocation?.solarSystemId != null -> "in {[location]targetSolarSystemID.name, linkify}"
            targetLocation?.constellationId != null -> "in the {[location]targetConstellationID.name, linkify} constellation"
            targetLocation?.regionId != null -> "in the {[location]targetRegionID.name, linkify} region"
            else -> null
        }
        val relativeLocation = when {
            targetLocation?.stationId != null && targetLocation.stationId == agentLocation?.stationId ->
                " This is the same station as your locator agent."
            targetLocation?.solarSystemId != null && targetLocation.solarSystemId == agentLocation?.solarSystemId ->
                " This is the same solar system as your locator agent."
            targetLocation?.constellationId != null && targetLocation.constellationId == agentLocation?.constellationId ->
                " This is the same constellation as your locator agent."
            targetLocation?.regionId != null && targetLocation.regionId == agentLocation?.regionId ->
                " This is the same region as your locator agent."
            else -> ""
        }
        val body = if (location != null) {
            "Your locator agent has found {[character]characterID.name, linkify} $location.$relativeLocation"
        } else {
            "Your locator agent found {[character]characterID.name, linkify}, but the reported location could not be resolved."
        }
        return formatDocumented(
            locationData,
            typeDetails,
            "Location of {[character]characterID.name, linkify}",
            body,
        )
    }

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `enemyID`: `123456`; type: Integer.
     */
    private fun formatMadeWarMutual(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your war against {enemyName} has been made mutual", "{charName} has made the war against {enemyName} mutual.")

    /**
     * Fields:
     * - `mercID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     */
    private fun formatMercOfferRetractedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{merc} has retracted its offer to serve in one of your wars", "{merc} has retracted its offer to ally with {defender} in its war against {aggressor}.")

    /**
     * Fields:
     * - `aggressorID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `iskValue`: `0.0`; type: Float.
     * - `mercID`: `123456`; type: Integer.
     */
    private fun formatMercOfferedNegotiationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{merc} has offered its services in one of your wars", "{merc} has offered {defender} its services in its war against {aggressor} for {iskOffered}")

    private fun formatMercenaryDenAttacked(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        fun percentage(field: String): Double? = (data[field] as? Number)?.toDouble()?.times(100)
        val formattedData = data + mapOf(
            "shieldPercentage" to percentage("shieldValue"),
            "armorPercentage" to percentage("armorValue"),
            "hullPercentage" to percentage("hullValue"),
            "aggressorCharacterID" to data["aggressorID"],
            "aggressorCorporationName" to if (data["aggressorCorpID"] == null) "Unknown" else null,
            "aggressorAllianceName" to if (data["aggressorAllianceID"] == null) "Unknown" else null,
        )
        return formatDocumented(
            formattedData,
            typeDetails,
            "Report: {[item]typeID.name, linkify} at {[location]planetID.name, linkify} is under attack",
            "Solar System: {[location]solarsystemID.name, linkify}<br>Attacked object: {[item]typeID.name, linkify} at {[location]planetID.name, linkify}<br>Current Shield Level: {[numeric]shieldPercentage, decimalPlaces=0}%<br>Current Armor Integrity: {[numeric]armorPercentage, decimalPlaces=0}%<br>Current Hull Integrity: {[numeric]hullPercentage, decimalPlaces=0}%<br>Aggressing Pilot: {[character]aggressorCharacterID.name, linkify}<br>Aggressing Pilot's Corporation: {aggressorCorporationName}<br>Aggressing Pilot's Alliance: {aggressorAllianceName}",
        )
    }

    private fun formatMercenaryDenNewMTO(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        if (data["job_link"] == null) return formatGeneric(data)
        return formatDocumented(data, typeDetails, "New Mercenary Tactical Operation Available", "{job_link} is available in {[location]solar_system_id.name, linkify}")
    }

    /**
     * Fields:
     * - `itemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     * - `timestampEntered`: `132537600000000000`; type: Integer.
     * - `timestampExited`: `132537600000000000`; type: Integer.
     * - `aggressorCharacterID`: `123456`; type: Integer.
     * - `aggressorCorporationID`: `123456`; type: Integer.
     * - `aggressorAllianceID`: `123456`; type: Integer.
     */
    private fun formatMercenaryDenReinforced(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name, linkinfo=mercenaryDenShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData} has entered reinforcement.", "The {[item]typeID.name, linkinfo=mercenaryDenShowInfoData} at planet {[location]planetID.name, linkinfo=planetShowInfoData} in {[location]solarsystemID.name, linkify} entered reinforcement at {[datetime]timestampEntered, date=short, time=short} and will remain in reinforcement until {[datetime]timestampExited, date=short, time=short}.\n\nAggressing Pilot: {[character]aggressorCharacterID.name, linkify}\nAggressing Pilot's Corporation: {aggressorCorporationName}\nAggressing Pilot's Alliance: {aggressorAllianceName}")

    private fun formatMissionCanceledTriglavian(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Mission Cancelled Due to Triglavian Control", "This mission has been cancelled as the agent issuing it or the location of its objective is in a Triglavian-controlled system. Missions will no longer be available in such systems.")

    /**
     * Fields:
     * - `body`: `[]`; type: Collection.
     * - `header`: `[]`; type: Collection.
     * - `objectiveDestinationID`: `123456`; type: Integer.
     * - `objectiveDestinationSystemID`: `123456`; type: Integer.
     * - `objectiveLocationID`: `123456`; type: Integer.
     * - `objectiveLocationSystemID`: `123456`; type: Integer.
     * - `objectiveQuantity`: `123456`; type: Integer.
     * - `objectiveTypeID`: `123456`; type: Integer.
     * - `rewardQuantity`: `123456`; type: Integer.
     * - `rewardTypeID`: `123456`; type: Integer.
     * - `missionKeywords`: `example`; type: String.
     */
    private fun formatMissionOfferExpirationMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val missionOfferExpirationText = mapOf(
            "UI/Agents/DefaultMessages/MissionOfferExpirationEmailHeader" to "Mission Offer Expired",
            "UI/Agents/DefaultMessages/MissionOfferExpirationEmailBody" to "I can no longer await your response to my offer. I am displeased by your indecisiveness.",
        )
        fun Any?.asMissionOfferExpirationText(): String? = when (this) {
            is String -> missionOfferExpirationText[this] ?: this
            is List<*> -> firstOrNull()?.toString()?.let(missionOfferExpirationText::get)
            else -> null
        }

        val header = data.findValue("header").asMissionOfferExpirationText() ?: return formatGeneric(data)
        val body = data.findValue("body").asMissionOfferExpirationText() ?: return formatGeneric(data)
        return formatDocumented(data, typeDetails, header, body)
    }

    private fun formatMissionTimeoutMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Time's up, {[character]notification_receiverID.name , linkify}", "You have failed the mission I gave you. I am disappointed in you. I was hoping for a little more competence.")

    /**
     * Fields:
     * - `moonID`: `123456`; type: Integer.
     * - `moonLink`: `example`; type: String.
     * - `oreVolumeByType`: `[]`; type: Collection.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `solarSystemLink`: `example`; type: String.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureName`: `example`; type: String.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatMoonminingAutomaticFracture(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatMoonMining(data, typeDetails, "Extraction for {structureLink} has autofractured", "The extraction for {structureLink}, at {moonLink} in {solarSystemLink} has reached the end of its lifetime and has fractured automatically.<br>The moon products are ready to be harvested.<br><br>Estimated ore composition:<br>{oreComp}")

    /**
     * Fields:
     * - `cancelledBy`: `123456`; type: Integer.
     * - `cancelledByLink`: `example`; type: String.
     * - `moonID`: `123456`; type: Integer.
     * - `moonLink`: `example`; type: String.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `solarSystemLink`: `example`; type: String.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureName`: `example`; type: String.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatMoonminingExtractionCancelled(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val body = if (data["cancelledBy"] != null) {
            "An ongoing extraction for {structureLink}, at {moonLink} in {solarSystemLink} has been cancelled by {cancelledByLink}."
        } else {
            "An ongoing extraction for {structureLink}, at {moonLink} in {solarSystemLink} has been cancelled."
        }
        return formatMoonMining(data, typeDetails, "Extraction cancelled for {structureLink}", body)
    }

    /**
     * Fields:
     * - `autoTime`: `132537600000000000`; type: Integer.
     * - `moonID`: `123456`; type: Integer.
     * - `moonLink`: `example`; type: String.
     * - `oreVolumeByType`: `[]`; type: Collection.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `solarSystemLink`: `example`; type: String.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureName`: `example`; type: String.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatMoonminingExtractionFinished(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatMoonMining(data, typeDetails, "Extraction finished for {structureLink}", "The extraction for {structureLink}, at {moonLink} in {solarSystemLink} is finished and the chunk is ready to be shot at.<br>The chunk will automatically fracture on {[datetime]autoTime, date=medium, time=short}.<br><br>Estimated ore composition:<br>{oreComp}")

    /**
     * Fields:
     * - `autoTime`: `132537600000000000`; type: Integer.
     * - `moonID`: `123456`; type: Integer.
     * - `moonLink`: `example`; type: String.
     * - `oreVolumeByType`: `[]`; type: Collection.
     * - `readyTime`: `132537600000000000`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `solarSystemLink`: `example`; type: String.
     * - `startedBy`: `123456`; type: Integer.
     * - `startedByLink`: `example`; type: String.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureName`: `example`; type: String.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatMoonminingExtractionStarted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatMoonMining(data, typeDetails, "Extraction started for {structureLink}", "An extraction has been started for {structureLink}, at {moonLink} in {solarSystemLink}.<br>Extraction was started by {startedByLink}.<br>The chunk will be ready in location on {[datetime]readyTime, date=full, time=short}, but will autofracture on {[datetime]autoTime, date=full, time=short}.<br><br>Estimated mineral composition:<br>{oreComp}")

    /**
     * Fields:
     * - `firedBy`: `123456`; type: Integer.
     * - `firedByLink`: `example`; type: String.
     * - `moonID`: `123456`; type: Integer.
     * - `moonLink`: `example`; type: String.
     * - `oreVolumeByType`: `[]`; type: Collection.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `solarSystemLink`: `example`; type: String.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureName`: `example`; type: String.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatMoonminingLaserFired(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatMoonMining(data, typeDetails, "Moondrill fired for {structureLink}", "The moondrill fitted to {structureLink}, at {moonLink} in {solarSystemLink} has been fired by {firedByLink} and the moon products are ready to be harvested.<br><br>Estimated ore composition:<br>{oreComp}")

    private fun formatMutualWarExpired(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Mutual war expires", "The mutual war between {declaredByName} and {againstName} has been invalidated by CONCORD for a lack of meaningful action in the last {numDays} days.")

    private fun formatMutualWarInviteAccepted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{againstName} accepted {declaredByName} invitation to a mutual war", "{againstName} accepted the invitation to a mutual war with {declaredByName}.<br><br>Hostilities can commence without CONCORD intervention after {[datetime]time}.")

    private fun formatMutualWarInviteRejected(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{againstName} declined mutual war invite", "{againstName} has declined a mutual invite sent by {declaredByName}.")

    private fun formatMutualWarInviteSent(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "A mutual war invite from {declaredByName} received", "{declaredByName} has invited {againstName} to a mutual war. The invite will expire on {[datetime]expireTimeStamp}.")

    private fun formatNPCStandingsChanged(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = eveNotificationTemplateFormatter.formatNpcStandingChange(data, typeDetails)

    /**
     * Fields:
     * - `rootArray`: `[]`; type: Collection.
     */
    private fun formatNPCStandingsGained(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = eveNotificationTemplateFormatter.formatNpcStandingChange(data, typeDetails)

    /**
     * Fields:
     * - `rootArray`: `[]`; type: Collection.
     */
    private fun formatNPCStandingsLost(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = eveNotificationTemplateFormatter.formatNpcStandingChange(data, typeDetails)

    /**
     * Fields:
     * - `allyID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `enemyID`: `123456`; type: Integer.
     * - `charID`: `123456`; type: Integer.
     */
    private fun formatOfferToAllyRetracted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "You have retracted your offer to ally with {defenderName}", "{charName} has retracted the offer from {allyName} to ally with {defenderName} in their war against {enemyName}.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `entityID`: `123456`; type: Integer.
     * - `iskValue`: `0.0`; type: Float.
     * - `offeredID`: `123456`; type: Integer.
     */
    private fun formatOfferedSurrender(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{entityName} offered to surrender", "{charName} has offered to surrender to {offeredName}, offering {iskOffer}. If accepted, the war will end in 24 hours and neither organization will be able to declare new wars against the other for the next 2 weeks.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     * - `enemyID`: `123456`; type: Integer.
     * - `iskValue`: `0.0`; type: Float.
     */
    private fun formatOfferedToAlly(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "You have offered to ally with {defenderName}", "{charName} has offered to ally to {defenderName} in their war against {enemyName}. The offer asked for {iskOffer} as payment.")

    private fun formatOfficeLeaseCanceledInsufficientStandings(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Corporation offices at {[location]station_id.name} were closed by {[npcOrganization]standings_from_id.name} due to insufficient standings.", "Renting offices at {[location]station_id.name, linkify} requires your corporation to have a standing of {[numeric]required_standing, decimalPlaces=2} with {[npcOrganization]standings_from_id.name}.\n\nSince your corporation has a standing of {[numeric]current_standing} with {[npcOrganization]standings_from_id.name} the office has been closed and its contents impounded.")

    /**
     * Fields:
     * - `subject`: `example`; type: String.
     * - `body`: `example`; type: String.
     */
    private fun formatOldLscMessages(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{subject}", "{body}")

    /**
     * Fields:
     * - `aggressorAllianceID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     * - `planetTypeID`: `123456`; type: Integer.
     * - `shieldLevel`: `0.0`; type: Float.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     */
    private fun formatOrbitalAttacked(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name} at planet {[location]planetID.name, linkinfo=planetLinkArgs} has been attacked", "{[item]typeID.name, linkify} at planet {[location]planetID.name, linkinfo=planetLinkArgs} has been attacked.<br> <br> Remaining Shields: {[numeric]shieldValue, decimalPlaces=1}%<br> Solar System: {[location]solarSystemID.name, linkify}<br> <br> Aggressor: {[character]aggressor.name, linkify}<br> Aggressor's Corporation: {aggressorCorp}<br> Aggressor's Alliance: {aggressorAlliance}")

    /**
     * Fields:
     * - `aggressorAllianceID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     * - `planetTypeID`: `123456`; type: Integer.
     * - `reinforceExitTime`: `132537600000000000`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     */
    private fun formatOrbitalReinforced(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name} at planet {[location]planetID.name, linkinfo=planetLinkArgs} has entered reinforced mode", "{[item]typeID.name, linkify} at planet {[location]planetID.name, linkinfo=planetLinkArgs} has recently entered reinforced mode.<br> Reinforced mode will end, and the {[item]typeID.name} will become vulnerable, at <b>{[datetime]reinforceExitTime, date=full, time=short}</b>.<br> Aggressor: {[character]aggressor.name, linkify}<br> Aggressor's Corporation: {aggressorCorp}<br> Aggressor's Alliance: {aggressorAlliance}")

    /**
     * Fields:
     * - `characterLinkData`: `[]`; type: Collection.
     * - `characterName`: `example`; type: String.
     * - `fromCorporationLinkData`: `[]`; type: Collection.
     * - `fromCorporationName`: `example`; type: String.
     * - `solarSystemLinkData`: `[]`; type: Collection.
     * - `solarSystemName`: `example`; type: String.
     * - `structureLinkData`: `[]`; type: Collection.
     * - `structureName`: `example`; type: String.
     * - `toCorporationLinkData`: `[]`; type: Collection.
     * - `toCorporationName`: `example`; type: String.
     */
    private fun formatOwnershipTransferred(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: \"{structureName, linkinfo=structureLinkData}\" ownership has been transferred to {toCorporationName, linkinfo=toCorporationLinkData}", "Ownership of structure \"{structureName, linkinfo=structureLinkData}\" in {solarSystemName, linkinfo=solarSystemLinkData} has been transferred from {fromCorporationName, linkinfo=fromCorporationLinkData} to {toCorporationName, linkinfo=toCorporationLinkData} by {characterName, linkinfo=characterLinkData}.")

    /**
     * Fields:
     * - `type_id`: `123456`; type: Integer.
     * - `ticket_count`: `example`; type: String.
     * - `ticket_price`: `0.0`; type: Float.
     */
    private fun formatRaffleCreated(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "HyperNet offer created", "Congratulations you have create a HyperNet offer for {type_link} with {node_count} HyperNodes at the cost of {node_price} per node.")

    /**
     * Fields:
     * - `type_id`: `123456`; type: Integer.
     * - `ticket_count`: `example`; type: String.
     * - `ticket_price`: `0.0`; type: Float.
     */
    private fun formatRaffleExpired(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "HyperNet offer expired!", "A HyperNet offer for {type_link} you are interested in has expired. Tickets price have been refunded and the item returned to its owner")

    /**
     * Fields:
     * - `type_id`: `123456`; type: Integer.
     * - `ticket_count`: `example`; type: String.
     * - `ticket_price`: `0.0`; type: Float.
     */
    private fun formatRaffleFinished(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "HyperNet offer completed", "A HyperNet offer for {type_link} you are taking part in has finished")

    /**
     * Fields:
     * - `addCloneInfo`: `123456`; type: Integer.
     * - `shipTypeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `stationID`: `123456`; type: Integer.
     */
    private fun formatReimbursementMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val subject = data["subject"] as? String
        val body = data["body"] as? String
        if (subject != null && body != null) return formatDocumented(data, typeDetails, subject, body)
        return formatDocumented(data, typeDetails, "Your ship has been salvaged", "Dear {[character]player.name},<br>A salvage operation recently recovered your {[item]shipTypeID.name} class ship from a debris field in {[location]solarSystemID.name}. You should be able to retrieve your ship at {[location]stationID.name}. The salvage team could not find any items that were ejected from your ship prior to its apparent destruction so if you were not able to retrieve the cargo canisters there is sadly nothing we can do. Law requires us to notify your friendly insurance company and they have deducted any financial payout from your account.<br><br>{signOff}")
    }

    private fun formatResearchMissionAvailableMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "I've encountered a problem.", "Our research has been fruitful, but I've encountered a snag and our research has been halted. Please contact me as soon as possible.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `enemyID`: `123456`; type: Integer.
     */
    private fun formatRetractsWar(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your war against {enemyName} has been retracted", "{charName} has retracted the war against {enemyName}. The war will end in 24 hours.")

    /**
     * Fields:
     * - `amount`: `0.0`; type: Float.
     */
    private fun formatSPAutoRedeemed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Completed goal skill point reward received", "You have been automatically transferred {[numeric]amount, useGrouping, decimalPlaces=0} unallocated skill points from an unclaimed reward you earned for completing a goal. Goal rewards are automatically transferred after 30 days if not manually claimed earlier.")

    /**
     * Fields:
     * - `challenge_name_id`: `example`; type: String.
     * - `max_progress`: `123456`; type: Integer.
     * - `points_awarded`: `123456`; type: Integer.
     * - `progress_text`: `example`; type: String.
     */
    private fun formatSeasonalChallengeCompleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        if (data["message_text"] != null) return formatGeneric(data)
        return formatDocumented(data, typeDetails, "Event challenge completed: {points_awarded} points earned", "Click here to view your progress")
    }

    private fun formatSkinSequencingCompleted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "SKIN Sequencing Complete", "Paragon is pleased to inform you that your new SKIN has finished Sequencing and has been delivered to your Collection.")

    /**
     * Fields:
     * - `itemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `timeLeft`: `132537600000000000`; type: Integer.
     */
    private fun formatSkyhookDeployed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData} has started onlining", "{[item]typeID.name, linkinfo=skyhookShowInfoData} at planet {[location]planetID.name, linkinfo=planetShowInfoData} belonging to {ownerCorpName, linkinfo=ownerCorpLinkData} is being onlined in {[location]solarsystemID.name, linkify} for the next {[timeinterval]timeLeft.writtenForm, from=hour, to=minute}. While it's onlining, it will not enter reinforcement.")

    /**
     * Fields:
     * - `itemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     */
    private fun formatSkyhookDestroyed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData} has been destroyed", "The {[item]typeID.name, linkinfo=skyhookShowInfoData} at planet {[location]planetID.name, linkinfo=planetShowInfoData} in {[location]solarsystemID.name, linkify} has been destroyed.")

    /**
     * Fields:
     * - `itemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     * - `reinforcedTimestamp`: `132537600000000000`; type: Integer.
     * - `vulnerableTime`: `132537600000000000`; type: Integer.
     */
    private fun formatSkyhookLostShields(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData} has lost its shields", "The {[item]typeID.name, linkinfo=skyhookShowInfoData} at planet {[location]planetID.name, linkinfo=planetShowInfoData} in {[location]solarsystemID.name, linkify} has lost its shields and is now in a reinforcement state for the next {[timeinterval]timeLeft.writtenForm, from=day, to=minute}. It will exit reinforcement at {[datetime]timestamp, date=short, time=short} and will then be vulnerable for {[timeinterval]vulnerableTime.writtenForm, from=hour, to=minute}.")

    /**
     * Fields:
     * - `itemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     */
    private fun formatSkyhookOnline(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData} is now online", "The {[item]typeID.name, linkinfo=skyhookShowInfoData} at planet {[location]planetID.name, linkinfo=planetShowInfoData} in {[location]solarsystemID.name, linkify} is now online.")

    /**
     * Fields:
     * - `itemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `planetID`: `123456`; type: Integer.
     * - `shield`: `example`; type: String.
     * - `armor`: `example`; type: String.
     * - `hull`: `example`; type: String.
     * - `isActive`: `false`; type: Boolean.
     * - `aggressorID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorAllianceID`: `123456`; type: Integer.
     */
    private fun formatSkyhookUnderAttack(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val body = if (data["allianceID"].isTruthy()) {
            "{warningText}Solar System: {[location]solarsystemID.name, linkify}\nAttacked object: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData}\nCurrent Shield Level: {[numeric]shieldPercentage, decimalPlaces=0}%\nCurrent Armor Integrity: {[numeric]armorPercentage, decimalPlaces=0}%\nCurrent Hull Integrity: {[numeric]hullPercentage, decimalPlaces=0}%\nAggressing Pilot: {[character]charID.name, linkify}\nAggressing Pilot's Corporation: {corpName, linkinfo=corpLinkData}\nAggressing Pilot's Alliance: {allianceName, linkinfo=allianceLinkData}"
        } else {
            "{warningText}Solar System: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData}\nCurrent Shield Level: {[numeric]shieldPercentage, decimalPlaces=0}%\nCurrent Armor Integrity: {[numeric]armorPercentage, decimalPlaces=0}%\nCurrent Hull Integrity: {[numeric]hullPercentage, decimalPlaces=0}%\nAggressing Pilot: {[character]charID.name, linkify}\nAggressing Pilot's Corporation: {corpName, linkinfo=corpLinkData}"
        }
        return formatDocumented(data, typeDetails, "Report: {[item]typeID.name, linkinfo=skyhookShowInfoData} at {[location]planetID.name, linkinfo=planetShowInfoData} is under attack", body)
    }

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovAllClaimAquiredMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "DED Sovereignty claim Acknowledgment: {[location]solarSystemID.name}", "This mail is your confirmation that DED now officially acknowledges that your member corporation {corporation} has claimed sovereignty on your behalf in the system {[location]solarSystemID.name, linkify}. For 24 hours following this message, a character with the Station Manager role in {corporation} can chose to either instantly capture or destroy any local Skyhook that is not already owned by your alliance. Any captured Skyhooks will become the property of {corporation}. If the Sovereignty Hub is transferred to another corporation within the alliance during this time window, then this capture/destroy ability will be transferred on to Station Managers within that corporation instead.")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovAllClaimLostMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Lost sovereignty in {[location]solarSystemID.name}", "DED acknowledges that you have lost your claim to sovereignty in the system {[location]solarSystemID.name, linkify}.")

    /**
     * Fields:
     * - `campaignEventType`: `123456`; type: Integer.
     * - `constellationID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovCommandNodeEventStarted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val subject = when ((data["campaignEventType"] as? Number)?.toInt()) {
            1 -> "Command nodes for the Territorial Claim Unit in {[location]solarSystemID.name} have begun to decloak"
            2 -> "Command nodes for the sovereignty hub in {[location]solarSystemID.name} have begun to decloak"
            3, 4 -> "Command nodes for a station in {[location]solarSystemID.name} have begun to decloak"
            else -> return formatGeneric(data)
        }
        return formatDocumented(data, typeDetails, subject, "Command nodes for this structure can now be found throughout the {[location]constellationID.name, linkify} constellation and capsuleers can use the Entosis Link to capture them.")
    }

    /**
     * Fields:
     * - `corpID`: `123456`; type: Integer.
     */
    private fun formatSovCorpBillLateMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Bill for {[location]solarSystemID.name} almost due", "Dear sovereign of {[location]solarSystemID.name, linkify}. It has been brought to our attention that the sovereignty registrar fee for {[location]solarSystemID.name, linkify}, claimed by {corporation}, is due in less then {dueInDays} days. We would therefore like to remind you that the failure to pay said bill before the specified due date will result in the immediate cancellation of {corporation} status as sovereign of {[location]solarSystemID.name, linkify}. Your friends at CONCORD.")

    /**
     * Fields:
     * - `reason`: `example`; type: String.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `allianceID`: `123456`; type: Integer.
     */
    private fun formatSovCorpClaimFailMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Sovereignty claim for {[location]solarSystemID.name} failed", "{body}")

    private fun formatSovDisruptorMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Sovereignty Blockade Unit activity detected in {[location]solarSystemID.name}.", "A new Sovereignty Blockade Unit has been detected in {[location]solarSystemID.name, linkify}.<br>If a Sovereignty Blockade Unit is online at more than half the stargates then your sovereignty claim in this system could be jeopardized.")

    /**
     * Fields:
     * - `freeportexittime`: `132537600000000000`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatSovStationEnteredFreeport(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name} has entered freeport mode", "The command nodes for this structure have been captured by hostile forces and it has automatically entered freeport mode until {[datetime]freeportexittime, date=full, time=short}")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatSovStructureDestroyed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name} has been destroyed", "The command nodes for this structure have been captured by hostile forces and it has automatically self-destructed.")

    /**
     * Fields:
     * - `campaignEventType`: `123456`; type: Integer.
     * - `decloakTime`: `132537600000000000`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovStructureReinforced(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val subject = when ((data["campaignEventType"] as? Number)?.toInt()) {
            1 -> "Territorial Claim Unit in {[location]solarSystemID.name} has entered reinforced mode"
            2 -> "Sovereignty hub in {[location]solarSystemID.name} has entered reinforced mode"
            3, 4 -> "Station in {[location]solarSystemID.name} has entered reinforced mode"
            else -> return formatGeneric(data)
        }
        return formatDocumented(data, typeDetails, subject, "This structure has been reinforced by hostile forces and command nodes will begin decloaking at {[datetime]decloakTime, date=full, time=short}")
    }

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatSovStructureSelfDestructCancel(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Self-destruct for {[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has been cancelled", "{[character]charID.name, linkify} has aborted the self-destruct sequence for the {[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify}.")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatSovStructureSelfDestructFinished(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has self-destructed", "The {[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has exploded after the completion of a manual self-destruct sequence.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `corpName`: `example`; type: String.
     * - `destructTime`: `132537600000000000`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatSovStructureSelfDestructRequested(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has initiated self-destruct", "{[character]charID.name, linkify} has started the self-destruct sequence for the {[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify}. Unless the sequence is aborted, this structure will explode at {[datetime]destructTime, date=full, time=short}. Any director of {corpName} may abort the self-destruct sequence by flying to the structure and rescinding the self-destruct command before {[datetime]destructTime, date=full, time=short}.")

    /**
     * Fields:
     * - `aggressorAllianceID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     * - `armorValue`: `0.0`; type: Float.
     * - `hullValue`: `0.0`; type: Float.
     * - `shieldValue`: `0.0`; type: Float.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovereigntyIHDamageMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your sovereignty hub in {[location]solarSystemID.name} is under attack!", "The sovereignty hub is being attacked in {[location]solarSystemID.name, linkify}. <br> <br> Attacker: {aggressor}<br> Corporation: {aggressorCorp}<br> Alliance: {aggressorAlliance}<br> <br> Sovereignty hub status is as follows:<br> Shield: {[numeric]shieldValue, decimalPlaces=1}%<br> Armor: {[numeric]armorValue, decimalPlaces=1}%<br> Structure: {[numeric]hullValue, decimalPlaces=1}%<br>")

    /**
     * Fields:
     * - `aggressorAllianceID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     * - `armorValue`: `0.0`; type: Float.
     * - `hullValue`: `0.0`; type: Float.
     * - `shieldValue`: `0.0`; type: Float.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovereigntySBUDamageMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your Sovereignty Blockade Unit in {[location]solarSystemID.name} is under attack!", "The Sovereignty Blockade Unit is being attacked in {[location]solarSystemID.name, linkify}. <br> <br> Attacker: {aggressor}<br> Corporation: {aggressorCorp}<br> Alliance: {aggressorAlliance}<br> <br> Sovereignty Blockade Unit status is as follows:<br> Shield: {[numeric]shieldValue, decimalPlaces=1}%<br> Armor: {[numeric]armorValue, decimalPlaces=1}%<br> Structure: {[numeric]hullValue, decimalPlaces=1}%<br>")

    /**
     * Fields:
     * - `aggressorAllianceID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     * - `armorValue`: `0.0`; type: Float.
     * - `hullValue`: `0.0`; type: Float.
     * - `shieldValue`: `0.0`; type: Float.
     * - `solarSystemID`: `123456`; type: Integer.
     */
    private fun formatSovereigntyTCUDamageMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your Territorial Claim Unit in {[location]solarSystemID.name} is under attack!", "The Territorial Claim Unit is being attacked in {[location]solarSystemID.name, linkify}. <br> <br> Attacker: {aggressor}<br> Corporation: {aggressorCorp}<br> Alliance: {aggressorAlliance}<br> <br> Territorial Claim Unit status is as follows:<br> Shield: {[numeric]shieldValue, decimalPlaces=1}%<br> Armor: {[numeric]armorValue, decimalPlaces=1}%<br> Structure: {[numeric]hullValue, decimalPlaces=1}%<br>")

    /**
     * Fields:
     * - `aggressorID`: `123456`; type: Integer.
     * - `shieldValue`: `0.0`; type: Float.
     * - `aggressorCorpName`: `example`; type: String.
     * - `aggressorCorpID`: `123456`; type: Integer.
     */
    private fun formatStationAggressionMsg1(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: \"{[item]typeID.name}\" is under attack at {[location]stationID.name}", "<br>Solar System: {[location]solarSystemID.name, linkify}<br>Current Shield Level: {[numeric]shieldDamage}%<br>{agressorText}")

    /**
     * Fields:
     * - `shieldValue`: `0.0`; type: Float.
     * - `armorValue`: `0.0`; type: Float.
     * - `hullValue`: `0.0`; type: Float.
     * - `aggressorID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorAllianceID`: `123456`; type: Integer.
     */
    private fun formatStationAggressionMsg2(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: Station '{[location]stationID.name}' is under attack", "The Outpost '{[location]stationID.name, linkify}' is being attacked in {[location]solarSystemID.name, linkify}.<br> <br> Attacker: {aggressor}<br> Corporation: {aggressorCorp}<br> Alliance: {aggressorAlliance}<br> <br> Outpost status is as follows:<br> Shield: {[numeric]shieldDamage, decimalPlaces=1}%<br> Armor: {[numeric]armorDamage, decimalPlaces=1}%<br> Structure: {[numeric]hullDamage, decimalPlaces=1}%<br>")

    /**
     * Fields:
     * - `oldOwnerID`: `123456`; type: Integer.
     * - `newOwnerID`: `123456`; type: Integer.
     * - `charID`: `123456`; type: Integer.
     */
    private fun formatStationConquerMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: Station '{[location]stationID.name, linkify}' has been conquered", "<br>Solar System: {[location]solarSystemID.name, linkify}<br>Conquered Corporation: {oldCorpName}<br>Conquering Corporation: {newCorpName}<br>{characterText}")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStationServiceDisabled(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has been disabled", "This station service has been disabled by hostile forces and must be reactivated with an Entosis Link before normal operation can resume.")

    /**
     * Fields:
     * - `solarSystemID`: `123456`; type: Integer.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStationServiceEnabled(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{[item]structureTypeID.name, linkify} in {[location]solarSystemID.name, linkify} has been enabled", "This station service has been successfully re-enabled by your alliance and normal operation can now resume.")

    /**
     * Fields:
     * - `state`: `example`; type: String.
     */
    private fun formatStationStateChangeMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{subject}", "Solar System: {[location]solarSystemID.name, linkify}")

    private fun formatStoryLineMissionAvailableMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "I am in need of your services, {[character]notification_receiverID.name, linkify}, for a very special mission.", "{body}")

    /**
     * Fields:
     * - `ownerCorpLinkData`: `[]`; type: Collection.
     * - `ownerCorpName`: `example`; type: String.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     * - `timeLeft`: `132537600000000000`; type: Integer.
     * - `vulnerableTime`: `132537600000000000`; type: Integer.
     */
    private fun formatStructureAnchoring(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} has started anchoring in {[location]solarsystemID.name, linkify}", "{[location]structureID.name, linkinfo=structureShowInfoData} belonging to {ownerCorpName, linkinfo=ownerCorpLinkData} is being deployed in {[location]solarsystemID.name, linkify} for the next {[timeinterval]timeLeft.writtenForm, from=hour, to=minute}, at the end of which it will become vulnerable for {[timeinterval]vulnerableTime.writtenForm, from=minute, to=minute}.")

    /**
     * Fields:
     * - `newStationID`: `123456`; type: Integer.
     * - `newStationTypeID`: `123456`; type: Integer.
     * - `newSolarSystemID`: `123456`; type: Integer.
     * - `oldSolarSystemID`: `123456`; type: Integer.
     * - `contractName`: `example`; type: String.
     * - `contractID`: `123456`; type: Integer.
     */
    private fun formatStructureCourierContractChanged(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Courier contract destination changed", "As the structure in {[location]oldSolarSystemID.name, linkinfo=oldSolarSystemDataLink} that served as the original destination of courier contract {contractName, linkinfo=contractData} has been destroyed or unanchored, the courier destination has automatically changed.<br>The new destination station for this courier contract is {[location]newStationID.name, linkinfo=newLocationData}.")

    /**
     * Fields:
     * - `ownerCorpLinkData`: `[]`; type: Collection.
     * - `ownerCorpName`: `example`; type: String.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureDestroyed(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has been destroyed", "The {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has been destroyed. Items located inside the structure are available for transfer by asset safety.")

    /**
     * Fields:
     * - `listOfTypesAndQty`: `[]`; type: Collection.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureFuelAlert(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} is low on resources in {[location]solarsystemID.name, linkify}", "This notification is to advise you that there are not enough fuel blocks present to support one full day of service module activity at {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify}.<br><br>Types and quantities present:<br>{typesAndQtyText}")

    /**
     * Fields:
     * - `daysUntilAbandon`: `123456`; type: Integer.
     * - `isCorpOwned`: `false`; type: Boolean.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureImpendingAbandonmentAssetsAtRisk(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Your assets located in a structure are at risk", "You have assets located at {structureLink} in the {[location]solarsystemID.name, linkify} system. These assets are at risk of loss as the structure is close to becoming abandoned.\nIn approximately {[numeric]daysUntilAbandon} {[numeric]daysUntilAbandon-> \"day\", \"days\"} this structure will become abandoned. Once this happens <b>any assets located in it will NOT be protected by asset safety transportation should the structure be destroyed</b>.\nYou can manually move your items within this structure into asset safety transportation at any time, as long as the structure remains in space.\nIf the structure owner reactivates the structure by powering up one or more service modules then the structure will no longer be at risk of abandonment.")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `listOfTypesAndQty`: `[]`; type: Collection.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureItemsDelivered(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Items delivered", "{[character]charID.name, linkify} has delivered the following items to {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify}\n\n{typesAndQtyText}")

    /**
     * Fields:
     * - `assetSafetyDurationFull`: `123456`; type: Integer.
     * - `assetSafetyDurationMinimum`: `123456`; type: Integer.
     * - `assetSafetyFullTimestamp`: `132537600000000000`; type: Integer.
     * - `assetSafetyMinimumTimestamp`: `132537600000000000`; type: Integer.
     * - `isCorpOwned`: `false`; type: Boolean.
     * - `newStationID`: `123456`; type: Integer.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureLink`: `example`; type: String.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureItemsMovedToSafety(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val isCorpOwned = data["isCorpOwned"].isTruthy()
        val subject = if (isCorpOwned) {
            "Report: corporation assets from {structureLink} in {[location]solarsystemID.name, linkify} have been moved into Asset Safety"
        } else {
            "Report: assets from {structureLink} in {[location]solarsystemID.name, linkify} have been moved into Asset Safety"
        }
        val body = if (isCorpOwned) {
            "Corporation Assets from {structureLink} in {[location]solarsystemID.name, linkify} are being moved by the Asset Safety system. In {[timeinterval]assetSafetyDurationMinimum.writtenForm, from=day, to=hour} (at {[datetime]assetSafetyMinimumTimestamp, date=short, time=short}), the assets can be manually delivered to a location in {[location]solarsystemID.name, linkify}. Control over manual delivery is only available to Directors.<br>In {[timeinterval]assetSafetyDurationFull.writtenForm, from=day, to=hour} (at {[datetime]assetSafetyFullTimestamp, date=short, time=short}), the assets will be automatically delivered to {[location]newStationID.name, linkify}. A fee will apply when retrieving the assets."
        } else {
            "Assets from {structureLink} in {[location]solarsystemID.name, linkify} are being moved by the Asset Safety system. In {[timeinterval]assetSafetyDurationMinimum.writtenForm, from=day, to=hour} (at {[datetime]assetSafetyMinimumTimestamp, date=short, time=short}), the assets can be manually delivered to a location in {[location]solarsystemID.name, linkify}.<br>In {[timeinterval]assetSafetyDurationFull.writtenForm, from=day, to=hour} (at {[datetime]assetSafetyFullTimestamp, date=short, time=short}), the assets will be automatically delivered to {[location]newStationID.name, linkify}. A fee will apply when retrieving the assets."
        }
        return formatDocumented(data, typeDetails, subject, body)
    }

    /**
     * Fields:
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     * - `timeLeft`: `132537600000000000`; type: Integer.
     * - `timestamp`: `132537600000000000`; type: Integer.
     * - `vulnerableTime`: `132537600000000000`; type: Integer.
     */
    private fun formatStructureLostArmor(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has lost its armor", "The {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has lost its armor and is now in a reinforcement state for the next {[timeinterval]timeLeft.writtenForm, from=day, to=minute}. It will exit reinforcement and will then be vulnerable for {[timeinterval]vulnerableTime.writtenForm, from=hour, to=minute}.")

    /**
     * Fields:
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     * - `timeLeft`: `132537600000000000`; type: Integer.
     * - `timestamp`: `132537600000000000`; type: Integer.
     * - `vulnerableTime`: `132537600000000000`; type: Integer.
     */
    private fun formatStructureLostShields(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has lost its shields", "The {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has lost its shields and is now in a reinforcement state for the next {[timeinterval]timeLeft.writtenForm, from=day, to=minute}. It will exit reinforcement and will then be vulnerable for {[timeinterval]vulnerableTime.writtenForm, from=hour, to=minute}.")

    private fun formatStructureLowReagentsAlert(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} is low on Magmatic Gas in {[location]solarsystemID.name, linkify}", "This notification is to advise you that there is insufficient Magmatic Gas present to support one full day of moon drill activity at {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify}.")

    private fun formatStructureNoReagentsAlert(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} requires more Magmatic Gas", "This notification is to advise you that {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has run out of Magmatic Gas, which is preventing further moon material production.")

    /**
     * Fields:
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureOnline(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} is now online", "The structure {[location]structureID.name, linkinfo=structureShowInfoData} is now online in {[location]solarsystemID.name, linkify} and can be fitted once it has finished a short repair cycle.")

    /**
     * Fields:
     * - `purchasedByCharacterID`: `123456`; type: Integer.
     * - `structureIDs`: `[]`; type: Collection.
     * - `lpAmount`: `0.0`; type: Float.
     * - `durationSeconds`: `example`; type: String.
     * - `issuedAt`: `false`; type: Boolean.
     * - `characterID`: `123456`; type: Integer.
     */
    private fun formatStructurePaintPurchased(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Nanocoating Induction License purchased for {[numeric]numStructures} corporation {[numeric]numStructures-> \"structure\", \"structures\"}", "Nanocoating Induction License has been purchased by {characterName, linkinfo=characterLinkData} for {[numeric]numStructures} corporation {[numeric]numStructures-> \"structure\", \"structures\"}, at a cost of {[numeric]lpAmount, useGrouping, decimalPlaces=0} EverMarks from the corporation LP wallet.\n<br>\nThe license is activated at {[datetime]issuedAt, date=medium, time=short} and is valid for {[timeinterval]duration.writtenForm, from=day, to=hour} from the date of purchase.")

    /**
     * Fields:
     * - `listOfServiceModuleIDs`: `[]`; type: Collection.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureServicesOffline(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: structure services have been offlined at {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify}", "This notification is to advise you that {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} has run out of fuel, which caused the following service modules to go offline:<br><br>{listOfServices}")

    /**
     * Fields:
     * - `ownerCorpLinkData`: `[]`; type: Collection.
     * - `ownerCorpName`: `example`; type: String.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     * - `timeLeft`: `132537600000000000`; type: Integer.
     */
    private fun formatStructureUnanchoring(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} has started unanchoring in {[location]solarsystemID.name, linkify}", "{[location]structureID.name, linkinfo=structureShowInfoData} belonging to {ownerCorpName, linkinfo=ownerCorpLinkData} is being removed in {[location]solarsystemID.name, linkify} for the next {[timeinterval]timeLeft.writtenForm, from=day, to=hour}, during which it will still be vulnerable to attack.")

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `allianceLinkData`: `[]`; type: Collection.
     * - `allianceName`: `example`; type: String.
     * - `armorPercentage`: `0.0`; type: Float.
     * - `charID`: `123456`; type: Integer.
     * - `corpLinkData`: `[]`; type: Collection.
     * - `corpName`: `example`; type: String.
     * - `hullPercentage`: `0.0`; type: Float.
     * - `shieldPercentage`: `0.0`; type: Float.
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureUnderAttack(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val body = if (data["allianceID"].isTruthy()) {
            "Solar System: {[location]solarsystemID.name, linkify}\nAttacked object: {[location]structureID.name, linkinfo=structureShowInfoData}\nCurrent Shield Level: {[numeric]shieldPercentage, decimalPlaces=0}%\nCurrent Armor Integrity: {[numeric]armorPercentage, decimalPlaces=0}%\nCurrent Hull Integrity: {[numeric]hullPercentage, decimalPlaces=0}%\nAggressing Pilot: {[character]charID.name, linkify}\nAggressing Pilot's Corporation: {corpName, linkinfo=corpLinkData}\nAggressing Pilot's Alliance: {allianceName, linkinfo=allianceLinkData}"
        } else {
            "Solar System: {[location]solarsystemID.name, linkify}\nAttacked object: {[location]structureID.name, linkinfo=structureShowInfoData}\nCurrent Shield Level: {[numeric]shieldPercentage, decimalPlaces=0}%\nCurrent Armor Integrity: {[numeric]armorPercentage, decimalPlaces=0}%\nCurrent Hull Integrity: {[numeric]hullPercentage, decimalPlaces=0}%\nAggressing Pilot: {[character]charID.name, linkify}\nAggressing Pilot's Corporation: {corpName, linkinfo=corpLinkData}"
        }
        return formatDocumented(data, typeDetails, "Report: {[location]structureID.name, linkinfo=structureShowInfoData} in {[location]solarsystemID.name, linkify} is under attack", body)
    }

    /**
     * Fields:
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureWentHighPower(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{structureLink} is now running at Full Power", "{structureLink} in {solarsystemLink} has at least one online service module and therefore is now operating at full power mode. While full power mode is active this structure will enjoy increased shield and armor hitpoints as well as an additional reinforcement cycle if attacked.")

    /**
     * Fields:
     * - `solarsystemID`: `123456`; type: Integer.
     * - `structureID`: `123456`; type: Integer.
     * - `structureShowInfoData`: `[]`; type: Collection.
     * - `structureTypeID`: `123456`; type: Integer.
     */
    private fun formatStructureWentLowPower(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{structureLink} is now running on Low Power", "{structureLink} in {solarsystemLink} does not have any online service modules and is therefore operating in low power mode. While in low power mode this structure will suffer reduced shield and armor hitpoints, and will have one less reinforcement cycle if attacked.")

    private fun formatStructuresJobsCancelled(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: industry jobs in {structureLink} have been cancelled", "One or more of your industry jobs in {structureLink} in {[location]solarsystemID.name, linkify} have been cancelled because the service module is no longer available.")

    private fun formatStructuresJobsPaused(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Report: industry jobs in {structureLink} have been paused", "One or more of your industry jobs in {structureLink} in {[location]solarsystemID.name, linkify} have been paused because the service modules they relied on are no longer online.")

    /**
     * Fields:
     * - `allStructureInfo`: `[]`; type: Collection.
     * - `hour`: `123456`; type: Integer.
     * - `numStructures`: `123456`; type: Integer.
     * - `timestamp`: `132537600000000000`; type: Integer.
     * - `weekday`: `123456`; type: Integer.
     */
    private fun formatStructuresReinforcementChanged(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "Reinforcement times changed for {[numeric]numStructures} {[numeric]numStructures -> \"structure\", \"structures\"}", "The following structures have had their reinforcement times changed:<br>{listOfStructureLinks}<br><br>The new reinforcements times will be <b>{weekday} at {reinforcementHour} (up to /- 3 hours)</b>.<br>The structure will change to the new reinforcement time at {[datetime]timestamp, date=short, time=short}, or at the next possible time after that when the structure is undamaged. ")

    /**
     * Fields:
     * - `aggressorAllianceID`: `123456`; type: Integer.
     * - `aggressorCorpID`: `123456`; type: Integer.
     * - `aggressorID`: `123456`; type: Integer.
     * - `armorValue`: `0.0`; type: Float.
     * - `hullValue`: `0.0`; type: Float.
     * - `moonID`: `123456`; type: Integer.
     * - `shieldValue`: `0.0`; type: Float.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     */
    private fun formatTowerAlertMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        fun percentage(field: String): Int? = (data[field] as? Number)?.toDouble()?.times(100)?.toInt()
        val shield = percentage("shieldValue")
        val armor = percentage("armorValue")
        val hull = percentage("hullValue")
        val status = if (shield != null && armor != null && hull != null) {
            "Current Shield Level: {[numeric]shieldDamage, leadingZeroes=2}%<br>Current Armor Integrity: {[numeric]armorDamage, leadingZeroes=2}%<br>Current Hull Integrity:  {[numeric]hullDamage, leadingZeroes=2}%<br>"
        } else {
            "Status: Missing, presumed destroyed.<br>"
        }
        val aggressorId = (data["aggressorID"] as? Number)?.toLong()
        val attacker = when (aggressorId?.let { typeDetails[it] }) {
            is TypeDetail.Character -> "Aggressing Pilot: {[character]aggressorID.name, linkify}<br>Aggressing Pilot's Corporation: {agressorCorp}<br>Aggressing Pilot's Alliance: {agressorAlliance}<br>"
            null -> ""
            else -> "Aggressor: {aggressorName}<br>"
        }
        val formattedData = data + mapOf(
            "shieldDamage" to shield,
            "armorDamage" to armor,
            "hullDamage" to hull,
            "agressorAlliance" to if (data["aggressorAllianceID"] == null) "-" else null,
            "moonName" to if (data["moonID"] == null) "unknown" else null,
        )
        return formatDocumented(
            formattedData,
            typeDetails,
            "Report: Starbase in {[location]solarSystemID.name, linkify} is under attack",
            "<br>Solar System: {[location]solarSystemID.name, linkify}<br>Moon: {moonName}<br><br>Attacked object: {[item]typeID.name, linkify}<br>$status$attacker",
        )
    }

    /**
     * Fields:
     * - `allianceID`: `123456`; type: Integer.
     * - `corpID`: `123456`; type: Integer.
     * - `moonID`: `123456`; type: Integer.
     * - `solarSystemID`: `123456`; type: Integer.
     * - `typeID`: `123456`; type: Integer.
     * - `quantity`: `123456`; type: Integer.
     * - `wants`: `example`; type: String.
     */
    private fun formatTowerResourceAlertMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val corporationAllianceText = if (data["corpID"].asLong()?.let { it != 0L } == true) {
            buildString {
                append("Corp: {corporation}")
                if (data["allianceID"].asLong() != null) append(", Alliance: {alliance}")
            }
        } else {
            ""
        }
        val wantsData = mutableMapOf<String, Any?>()
        val wantsText = (data["wants"] as? List<*>)
            ?.mapNotNull { it as? Map<*, *> }
            ?.mapIndexed { index, rawWant ->
                val typeField = "want${index}TypeID"
                val quantityField = "want${index}Quantity"
                wantsData[typeField] = rawWant.findValue("typeID")
                wantsData[quantityField] = rawWant.findValue("quantity")
                "Type: {[item]$typeField.name, linkify}<br>Quantity: {[numeric]$quantityField}<br>"
            }
            ?.joinToString("")
            .orEmpty()
        val body = "<br>This notification is to advise you that there are not enough resources present to last a day of full Starbase activity.<br><br>Solar System: {[location]solarSystemID.name, linkify}<br>Moon: {[location]moonID.name, linkify}, Type: {[item]typeID.name, linkify}<br>$corporationAllianceText<br><br>Types and quantities present:<br>$wantsText"
        return formatDocumented(
            data + wantsData,
            typeDetails,
            "Report: Starbase low on resources in {[location]solarSystemID.name}",
            body,
        )
    }

    private fun formatTransactionReversalMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "You have incurred reversed transactions", "{body}")

    /**
     * Fields:
     * - `charID`: `123456`; type: Integer.
     * - `corpName`: `example`; type: String.
     * - `stationID`: `123456`; type: Integer.
     * - `agentID`: `123456`; type: Integer.
     */
    private fun formatTutorialMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        val characterId = data.findValue("charID").asLong()
            ?: data.findValue("notification_receiverID").asLong()
        val agentId = data.findValue("agentID").asLong()
            ?: data.findValue("notification_senderID").asLong()
        val tutorialData = data + mapOf(
            "tutorialCharacterID" to characterId,
            "tutorialAgentID" to agentId,
        )
        val body = if (typeDetails[agentId] != null) {
            buildString {
                append("A tutorial message for {[character]tutorialCharacterID.name, linkify} from {[character]tutorialAgentID.name, linkify}")
                data.findValue("corpName")?.toString()?.takeIf(String::isNotBlank)?.let {
                    append(", representing {corpName}")
                }
                if (data.findValue("stationID").asLong() != null) {
                    append(" at {[location]stationID.name, linkify}")
                }
                append(".")
            }
        } else {
            "The agent details for this tutorial message to {[character]tutorialCharacterID.name, linkify} are no longer available."
        }
        return formatDocumented(tutorialData, typeDetails, "Tutorial message", body)
    }

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `allianceID`: `123456`; type: Integer.
     * - `declaredByID`: `123456`; type: Integer.
     */
    private fun formatWarAdopted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{allianceName} is now defending for {againstName}.", "{againstName} has joined {allianceName}.  \n\nAs a result {allianceName} has taken over the defenses of {againstName} in their war with {declaredByName}.")

    /**
     * Fields:
     * - `allianceName`: `example`; type: String.
     * - `allianceID`: `123456`; type: Integer.
     * - `allyText`: `example`; type: String.
     */
    private fun formatWarAllyInherited(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "War update: {quitterName} has left {allianceName}", "There has been a development in the war between {declaredByName} and {againstName}. {quitterName} is no longer a member of {allianceName}, and therefore a new war between {opponentName} and {quitterName} has begun. This war is subject to all the standard CONCORD rules for formal capsuleer warfare including war eligibility, war bills, and war headquarters. If any of the standard war requirements are not met the war will be declared invalid and enter a cooldown period. As an ally to {allianceName}, your obligations are retained in that conflict.")

    /**
     * Fields:
     * - `aggressorID`: `123456`; type: Integer.
     * - `allyID`: `123456`; type: Integer.
     * - `charID`: `123456`; type: Integer.
     * - `defenderID`: `123456`; type: Integer.
     */
    private fun formatWarAllyOfferDeclinedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{defender} has declined {ally} offer of assistance", "{char} has declined assistance from {ally} on behalf of {defender} in its war against {aggressor}.")

    private fun formatWarConcordInvalidates(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "CONCORD invalidates war declared by {declaredByName} against {againstName}", "CONCORD has declared this war invalid as it breaches one or more articles in the Yulai Convention. The war will be declared as being over after approximately 24 hours.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `cost`: `0.0`; type: Float.
     * - `declaredByID`: `123456`; type: Integer.
     * - `delayHours`: `123456`; type: Integer.
     * - `hostileState`: `false`; type: Boolean.
     * - `timeStarted`: `132537600000000000`; type: Integer.
     * - `warHQ`: `example`; type: String.
     * - `warHQ_IdType`: `[]`; type: Collection.
     */
    private fun formatWarDeclared(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification {
        return eveNotificationTemplateFormatter.formatWarDeclared(
            data = data,
            typeDetails = typeDetails,
            subject = "{declaredByName} Declares War Against {againstName}",
            hostileWithHeadquartersBody = "{declaredByName} has declared war on {againstName}, designating {warHQ} as the headquarters for the war.<br>Fighting can legally occur between those involved.<br><br>{costText}",
            hostileBody = "{declaredByName} has declared war on {againstName}.<br>Fighting can legally occur between those involved.<br><br>{costText}",
            delayedWithHeadquartersBody = "{declaredByName} has declared war on {againstName} with {warHQ} as the designated war headquarters.\n\nWithin {delayHours} hours fighting can legally occur between those involved.  {costText}",
            withHeadquartersBody = "{declaredByName} has declared war on {againstName} using <b>{warHQ}</b> as the designated war headquarters structure.<br><br>Within 24 hours fighting can legally occur between those involved. If war is due to a corporation at war joining or leaving an alliance, then the war starts immediately instead.<br><br>{costText}",
            defaultBody = "{declaredByName} has declared war on {againstName}.\n\nWithin {delayHours} hours fighting can legally occur between those involved. {costText}",
        )
    }

    /**
     * Fields:
     * - `warHQ_IdType`: `example`; type: String.
     * - `warHQ`: `example`; type: String.
     */
    private fun formatWarEndedHqSecurityDrop(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "War declared by {declaredByName} against {againstName} ending", "The war between {declaredByName} and {againstName} is coming to an end. The war headquarters {warHQ} chosen by {declaredByName} are in a solar system that is no longer fully protected by CONCORD. The war will be declared as being over after approximately 24 hours.\n\nAfter {[datetime]endDate, date=full, time=short} CONCORD will again respond to any hostilities between those involved with full force.\n\n{signOff}")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `declaredByID`: `123456`; type: Integer.
     * - `timeDeclared`: `132537600000000000`; type: Integer.
     * - `warHQ`: `example`; type: String.
     */
    private fun formatWarHQRemovedFromSpace(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "WarHQ {warHQ} lost", "The war HQ <b>{warHQ}</b> is no more.  As a consequence, the war declared by {declaredByName} against {againstName} on {declaredDate} has been declared invalid by CONCORD and has entered its cooldown period.  \n\n{declaredByName} will be barred from declaring further wars against {againstName} for two weeks.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `allianceID`: `123456`; type: Integer.
     * - `declaredByID`: `123456`; type: Integer.
     * - `opponentID`: `123456`; type: Integer.
     * - `quitterID`: `123456`; type: Integer.
     */
    private fun formatWarInherited(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "War update: {quitterName} has left {allianceName}", "There has been a development in the war between {declaredByName} and {againstName}. \n\n{quitterName} is no longer a member of {allianceName}, and therefore a new war between {opponentName} and {quitterName} has begun. This war is subject to all the standard CONCORD rules for formal capsuleer warfare including war eligibility, war bills, and war headquarters. If any of the standard war requirements are not met the war will be declared invalid and enter a cooldown period.\n\n{allyText}")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `declaredByID`: `123456`; type: Integer.
     * - `endDate`: `132537600000000000`; type: Integer.
     */
    private fun formatWarInvalid(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "CONCORD invalidates war", "The war between {declaredByName} and {againstName} has been retracted by CONCORD because at least one of the involved parties has become ineligible for war declarations.\n\nFighting must cease on {[datetime]endDate, date=full, time=short}.\n\n{signOff}")

    private fun formatWarRetracted(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{declaredByName} Retracts War Against {againstName}", "The war between {declaredByName} and {againstName} is coming to an end. {declaredByName} has retracted the war against {againstName}. The war will be declared as being over after approximately 24 hours.")

    /**
     * Fields:
     * - `againstID`: `123456`; type: Integer.
     * - `declaredByID`: `123456`; type: Integer.
     * - `endDate`: `132537600000000000`; type: Integer.
     */
    private fun formatWarRetractedByConcord(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "CONCORD invalidates war", "The war between {declaredByName} and {againstName} has been retracted by CONCORD as the appropriate bills for the conflict were not paid.\\nAfter {[datetime]endDate, date=full, time=short} CONCORD will again respond to any hostilities between those involved with full force.\\n{signOff}")

    /**
     * Fields:
     * - `iskValue`: `0.0`; type: Float.
     * - `ownerID`: `123456`; type: Integer.
     */
    private fun formatWarSurrenderDeclinedMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{owner} has declined your surrender offer", "{owner} has declined your offer of surrender in exchange for the sum of {iskOffered}")

    /**
     * Fields:
     * - `iskValue`: `0.0`; type: Float.
     * - `ownerID1`: `123456`; type: Integer.
     * - `ownerID2`: `123456`; type: Integer.
     * - `warNegotiationID`: `123456`; type: Integer.
     */
    private fun formatWarSurrenderOfferMsg(data: Map<String, Any?>, typeDetails: TypeDetails): ParsedEveNotification = formatDocumented(data, typeDetails, "{owner1} has offered a surrender", "{owner1} has offered to end the war in the exchange for {iskOffered}. If accepted, the war will end in 24 hours and your organizations will be unable to declare new wars against each other for the next 2 weeks. Click <a href=\"warNegotiation:{warNegotiationID}\">here</a> to respond to their offer.")

    private fun Any?.isTruthy(): Boolean = when (this) {
        is Boolean -> this
        is Number -> toDouble() != 0.0
        is String -> equals("true", ignoreCase = true) || toDoubleOrNull()?.let { it != 0.0 } == true
        else -> false
    }

    private fun Any?.asLong(): Long? = when (this) {
        is Number -> toLong()
        is String -> toLongOrNull()
        else -> null
    }

    private fun Any?.asDouble(): Double? = when (this) {
        is Number -> toDouble()
        is String -> toDoubleOrNull()
        else -> null
    }

    private fun Map<*, *>.findValue(key: String): Any? {
        return entries.firstOrNull { entry -> entry.key?.toString()?.equals(key, ignoreCase = true) == true }?.value
    }

    private data class LocatorLocation(
        val regionId: Long?,
        val constellationId: Long?,
        val solarSystemId: Long?,
        val stationId: Long?,
    )

    private fun Map<*, *>.toLocatorLocation() = LocatorLocation(
        regionId = findLocationId(groupId = 3, field = "regionID"),
        constellationId = findLocationId(groupId = 4, field = "constellationID"),
        solarSystemId = findLocationId(groupId = 5, field = "solarSystemID"),
        stationId = findLocationId(groupId = 15, field = "stationID"),
    )

    private fun Map<*, *>.findLocationId(groupId: Int, field: String): Long? {
        return entries.firstOrNull { [key] -> key?.toString()?.toIntOrNull() == groupId }?.value.asLong()
            ?: findValue(field).asLong()
    }
}
