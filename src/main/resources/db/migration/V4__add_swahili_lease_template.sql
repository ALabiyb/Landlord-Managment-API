-- =====================================================
-- Add Swahili Lease Template
-- =====================================================

INSERT INTO contract_templates (id, name, content, description, is_active, created_at, updated_at)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 
    'Swahili Residential Lease (Makazi)', 
    'MKATABA WA KUPANGISHA NYUMBA YA MAKAZI
MKATABA HUU umetengenezwa tarehe {{leaseStartDate}} hadi {{leaseEndDate}}
KATI YA
 {{landlordName}} wa {{landlordAddress}} (ambaye kwa madhumuni ya makubaliano hayaatajulikana kama "MWENYE NYUMBA") kwa upande mwingine
NA
{{tenantName}} wa Simu Na {{tenantPhoneNumber}}, {{houseDistrict}} (hapa kujulikana kama "MPANGAJI") kwa upande mwingine;

A. KWAMBA: MWENYE NYUMBA ni mmiliki halali wa nyumba iliyopo {{houseAddress}}, (hapa kujulikana kama ''APARTMENT''.)
B. KWAMBA: MPANGAJI ana nia ya kupanga katika "apartment" ya vyumba viwili kwa matumizi ya MAKAZI na MWENYENYUMBA ana nia ya kumpangisha MPANGAJI kwa matumizi ya MAKAZI.

C. NA KWAMBA: MWENYE NYUMBA na MPANGAJI wanakubaliana kwa pamoja kuzingatia na kuheshimu taratibu na masharti yaliyowekwa katika Mkataba huu; hapa yatajulikana kama ''MAKUBALIANO YA MKATABA''.

D. NA KWAMBA: MWENYE NYUMBA anamhakikishia MPANGAJI kwamba "apartment" iliyotajwa hapo hapo juu anaimiliki na kuisimamia kihalali na haina au kizuizi chochote kinachoweza kusababisha mgogoro na watu/mtu/taasisi au mamlaka yoyote ile.
 
KWAHIYO BASI MKATABA HUU UNATHIBITISHA YAFUATAYO:

1. ENEO LINALOPANGISHWA: 
MWENYE NYUMBA anampangisha MPANGAJI, "apartment" iliyopo katika {{houseAddress}}.

2. MUDA WA MKATABA:
2.1. Mkataba huu ni wa kipindi cha miezi {{leaseDuration}} na utaanza tarehe {{leaseStartDate}} na utaisha tarehe {{leaseEndDate}}.
2.2. Na endapo MPANGAJI atataka kuendelea na mkataba, baada ya tarehe {{leaseEndDate}} MPANGAJI atatoa notisi ya mwezi mmoja ya kusudio la kutaka kuendelea au kuhuisha mkataba wa pango. Kisha pande mbili zitaingia mkataba mwingine wa upangishaji ukiwa na masharti mengine ambayo yatakubaliwa na pande mbili.

3. KODI YA PANGO NA GHARAMA ZA HUDUMA:

3.1 Kodi ya pango kwa mwezi itakuwa Shilingi {{rentAmount}} tu. Kodi hiyo italipwa kwa kuweka katika akaunti Na.{{bankAccount}} iliyopo benki ya {{bankName}} ikiwa na jina la {{landlordName}}.

3.2 MPANGAJI amelipa kodi ya pango ya mwezi na fedha za tahadhari. Kodi ya mwezi ni {{rentAmount}} na fedha za tahadhari ni {{securityDeposit}}.

3.3 MWENYE NYUMBA amesaini mkataba huu leo tarehe {{currentDate}} kukiri na kuthibitisha kupokea malipo toka kwa MPANGAJI.

3.4 MPANGAJI ameweka fedha ya tahadhari (Security Deposit) ya kiasi cha {{securityDeposit}}. Pesa hiyo itarejeshwa baada ya kuisha mkataba huo wa pango na pande zote mbili kuridhia kutohuishwa kwa mkataba huu wa Pango.

4.0. MATUMIZI YA APARTMENT:
       MPANGAJI anatakiwa kuzingatia matumizi yafuatayo:
- Eneo hilo amepangishwa kwa ajili ya MAKAZI tu.
- Kuhakikisha anaweka eneo lake na eneo linalomzunguka katika mazingira ya usafi wakati wote.
- MPANGAJI haruhusiwi kuweka mifugo yeyote.
- MPANGAJI haruhusiwi kusababisha vurugu au kelele.

12.0. UTATUZI WA MGOGORO
Endapo itatokea mgogoro wowote baina ya MWENYENYUMBA na MPANGAJI kuhusiana na tafsiri au utekelezaji wa mkataba huu. Pande mbili zitaketi na kusuluhisha mngogoro huu.

KWA KUTHIBITISHA haya yote yaliyotamkwa katika mkataba huu hapo juu pande zote mbili wameweka saini zao hapa chini siku na tarehe iliyoonyeshwa:-
MWENYE NYUMBA: {{landlordName}}
MPANGAJI: {{tenantName}}
Tarehe: {{currentDate}}', 
    'Standard Swahili Residential Lease Template with placeholders',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
