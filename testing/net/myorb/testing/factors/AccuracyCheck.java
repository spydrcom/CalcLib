
package net.myorb.testing.factors;

/**
 * evaluate precision of computed approximations
 * @author Michael Druckman
 */
public class AccuracyCheck
{


	/**
	 * verify value accuracy against reference
	 * @param reference the reference to be used
	 * @param comparedWith a string to match with reference digits
	 * @return the position where match fails checking character by character
	 * @throws RuntimeException when reference is not adequate
	 */
	public static int difAt (String reference, String comparedWith) throws RuntimeException
	{
		int most = Math.min (comparedWith.length (), reference.length ());
		for (int i=0; i<most;i++) { if (reference.charAt(i) != comparedWith.charAt(i)) return i; }
		throw new RuntimeException ("Resulting accuracy greater than reference");
	}


	public static String PI = // 3000 decimal digit reference
		"3.1415926535 8979323846 2643383279 5028841971 6939937510 5820974944 5923078164 0628620899 8628034825 3421170679  " +
        "  8214808651 3282306647 0938446095 5058223172 5359408128 4811174502 8410270193 8521105559 6446229489 5493038196  " +
        "  4428810975 6659334461 2847564823 3786783165 2712019091 4564856692 3460348610 4543266482 1339360726 0249141273  " +
        "  7245870066 0631558817 4881520920 9628292540 9171536436 7892590360 0113305305 4882046652 1384146951 9415116094  " +
        "  3305727036 5759591953 0921861173 8193261179 3105118548 0744623799 6274956735 1885752724 8912279381 8301194912  " + // 500
        "  9833673362 4406566430 8602139494 6395224737 1907021798 6094370277 0539217176 2931767523 8467481846 7669405132  " +
        "  0005681271 4526356082 7785771342 7577896091 7363717872 1468440901 2249534301 4654958537 1050792279 6892589235  " +
        "  4201995611 2129021960 8640344181 5981362977 4771309960 5187072113 4999999837 2978049951 0597317328 1609631859  " + 
        "  5024459455 3469083026 4252230825 3344685035 2619311881 7101000313 7838752886 5875332083 8142061717 7669147303  " +
        "  5982534904 2875546873 1159562863 8823537875 9375195778 1857780532 1712268066 1300192787 6611195909 2164201989  " + // 1000
        "  3809525720 1065485863 2788659361 5338182796 8230301952 0353018529 6899577362 2599413891 2497217752 8347913151  " +
        "  5574857242 4541506959 5082953311 6861727855 8890750983 8175463746 4939319255 0604009277 0167113900 9848824012  " +
        "  8583616035 6370766010 4710181942 9555961989 4676783744 9448255379 7747268471 0404753464 6208046684 2590694912  " +
        "  9331367702 8989152104 7521620569 6602405803 8150193511 2533824300 3558764024 7496473263 9141992726 0426992279  " +
        "  6782354781 6360093417 2164121992 4586315030 2861829745 5570674983 8505494588 5869269956 9092721079 7509302955  " + // 1500
        "  3211653449 8720275596 0236480665 4991198818 3479775356 6369807426 5425278625 5181841757 4672890977 7727938000  " +
        "  8164706001 6145249192 1732172147 7235014144 1973568548 1613611573 5255213347 5741849468 4385233239 0739414333  " +
        "  4547762416 8625189835 6948556209 9219222184 2725502542 5688767179 0494601653 4668049886 2723279178 6085784383  " +
        "  8279679766 8145410095 3883786360 9506800642 2512520511 7392984896 0841284886 2694560424 1965285022 2106611863  " +
        "  0674427862 2039194945 0471237137 8696095636 4371917287 4677646575 7396241389 0865832645 9958133904 7802759009  " + // 2000
		"  9465764078 9512694683 9835259570 9825822620 5224894077 2671947826 8482601476 9909026401 3639443745 5305068203  " +
		"  4962524517 4939965143 1429809190 6592509372 2169646151 5709858387 4105978859 5977297549 8930161753 9284681382  " +
		"  6868386894 2774155991 8559252459 5395943104 9972524680 8459872736 4469584865 3836736222 6260991246 0805124388  " +
		"  4390451244 1365497627 8079771569 1435997700 1296160894 4169486855 5848406353 4220722258 2848864815 8456028506  " +
		"  0168427394 5226746767 8895252138 5225499546 6672782398 6456596116 3548862305 7745649803 5593634568 1743241125  " + // 2500
 		"  1507606947 9451096596 0940252288 7971089314 5669136867 2287489405 6010150330 8617928680 9208747609 1782493858  " +
		"  9009714909 6759852613 6554978189 3129784821 6829989487 2265880485 7564014270 4775551323 7964145152 3746234364  " +
		"  5428584447 9526586782 1051141354 7357395231 1342716610 2135969536 2314429524 8493718711 0145765403 5902799344  " +
		"  0374200731 0578539062 1983874478 0847848968 3321445713 8687519435 0643021845 3191048481 0053706146 8067491927  " +
		"  8191197939 9520614196 6342875444 0643745123 7181921799 9839101591 9561814675 1426912397 4894090718 6494231961  "   // 3000
        ;
	//		taken from http://web.archive.org/web/20140225153300/http://www.exploratorium.edu/pi/pi_archive/Pi10-6.html
	public static String PI_REF = PI.replaceAll (" ", "");


	public static String S2 = // 3500 decimal digit reference
		"1.4142135623 7309504880 1688724209 6980785696 7187537694  8073176679 7379907324 7846210703 8850387534 3276415727  " + 
		"  3501384623 0912297024 9248360558 5073721264 4121497099  9358314132 2266592750 5592755799 9505011527 8206057147  " +
		"  0109559971 6059702745 3459686201 4728517418 6408891986  0955232923 0484308714 3214508397 6260362799 5251407989  " +
		"  6872533965 4633180882 9640620615 2583523950 5474575028  7759961729 8355752203 3753185701 1354374603 4084988471  " +
		"  6038689997 0699004815 0305440277 9031645424 7823068492  9369186215 8057846311 1596668713 0130156185 6898723723  " + // 500
		"  5288509264 8612494977 1542183342 0428568606 0146824720  7714358548 7415565706 9677653720 2264854470 1585880162  " +
		"  0758474922 6572260020 8558446652 1458398893 9443709265  9180031138 8246468157 0826301005 9485870400 3186480342  " +
		"  1948972782 9064104507 2636881313 7398552561 1732204024  5091227700 2269411275 7362728049 5738108967 5040183698  " +
		"  6836845072 5799364729 0607629969 4138047565 4823728997  1803268024 7442062926 9124859052 1810044598 4215059112  " +
		"  0249441341 7285314781 0580360337 1077309182 8693147101  7111168391 6581726889 4197587165 8215212822 9518488472  " + // 1000
		"  0896946338 6289156288 2765952635 1405422676 5323969461  7511291602 4087155101 3515045538 1287560052 6314680171  " +
		"  2740265396 9470240300 5174953188 6292563138 5188163478  0015693691 7688185237 8684052287 8376293892 1430065586  " +
		"  9568685964 5951555016 4472450983 6896036887 3231143894  1557665104 0883914292 3381132060 5243362948 5317049915  " +
		"  7717562285 4974143899 9188021762 4309652065 6421182731  6726257539 5947172559 3463723863 2261482742 6222086711  " +
		"  5583959992 6521176252 6989175409 8815934864 0083457085  1814722318 1420407042 6509056532 3333984364 5786579679  " + // 1500
		"  6519267292 3998753666 1721598257 8860263363 6178274959  9421940377 7753681426 2177387991 9455139723 1274066898  " +
		"  3299898953 8672882285 6378697749 6625199665 8352577619  8939322845 3447356947 9496295216 8891485492 5389047558  " +
		"  2883452609 6524096542 8893945386 4662574492 7556381964  4103169798 3306185201 9379384940 0571563337 2054806854  " +
		"  0575867999 6701213722 3947582142 6306585132 2174088323  8294728761 7393647467 8374319600 0159218880 7347857617  " +
		"  2522118674 9042497736 6929207311 0963697216 0893370866  1156734585 3348332952 5467585164 4710757848 6024636008  " + // 2000
		"  3444911481 8587655554 2864551233 1421992631 1332517970  6084365597 0435285641 0087918500 7603610091 5946567067  " +
		"  6883605571 7400767569 0509613671 9401324935 6052401859  9910506210 8163597726 4313806054 6701029356 9971042425  " +
		"  1057817495 3105725593 4984451126 9227803449 1350663756  8747760283 1628296055 3242242695 7534529028 8387684464  " +
		"  2917328277 0888318087 0253398523 3812274999 0812371892  5407264753 6785030482 1591801886 1671089728 6922920119  " +
		"  7599880703 8185433325 3646021108 2299279293 0728717807  9988809917 6741774108 9830608003 2631181642 7988231171  " + // 2500
		"  5436386966 1702999934 1616148786 8601804550 5553986913  1151860103 8637532500 4558186044 8040750241 1951843056  " +
		"  7453368361 3674597374 4239885532 8517930896 0373898915  1731958741 3442881784 2125021916 9518755934 4438739618  " +
		"  9314549999 9061075870 4909026088 3517636224 7497578588  5836803745 7931157339 8020999866 2218694992 2595913276  " +
		"  4236194105 9210032802 6149874566 5996888740 6795616739  1859572888 6424734635 8588686449 6822386006 9833526427  " +
		"  9905628316 5613913942 5576490620 6518602164 7263033362  9750756978 7060660685 6498160092 7187092921 5313236828  " + // 3000
		"  1356988937 0974165044 7459096053 7472796524 4770940992  4123871061 4470543986 7436473384 7745481910 0872886222  " +
		"  1495895295 9118789214 9179833981 0837882781 5306556231  5810360648 6758730360 1450227320 8829351341 3872276841  " +
		"  7667843690 5294286984 9083845574 4579409598 6260742499  5491680285 3077398938 2960362133 5398753205 0919989360  " +
		"  7513906444 4957684569 9347127636 4507163279 1547015977  3354863893 9423257277 5400382602 7478567417 2580951416  " +
		"  3071595978 4981800944 3560379390 9855901682 7215403458  1581521004 9366629534 4882710729 2396602321 6382382666  "   // 3500
		;
	//		taken from https://nerdparadise.com/math/reference/2sqrt10000
	public static String S2_REF = S2.replaceAll (" ", "");


	public static String E = // 3000 decimal digit reference
		"2.7182818284 5904523536 0287471352 6624977572 4709369995 9574966967 6277240766 3035354759 4571382178 5251664274 "+
		"  2746639193 2003059921 8174135966 2904357290 0334295260 5956307381 3232862794 3490763233 8298807531 9525101901 "+
		"  1573834187 9307021540 8914993488 4167509244 7614606680 8226480016 8477411853 7423454424 3710753907 7744992069 "+
		"  5517027618 3860626133 1384583000 7520449338 2656029760 6737113200 7093287091 2744374704 7230696977 2093101416 "+
		"  9283681902 5515108657 4637721112 5238978442 5056953696 7707854499 6996794686 4454905987 9316368892 3009879312 "+ // 500
		"  7736178215 4249992295 7635148220 8269895193 6680331825 2886939849 6465105820 9392398294 8879332036 2509443117 "+
		"  3012381970 6841614039 7019837679 3206832823 7646480429 5311802328 7825098194 5581530175 6717361332 0698112509 "+
		"  9618188159 3041690351 5988885193 4580727386 6738589422 8792284998 9208680582 5749279610 4841984443 6346324496 "+
		"  8487560233 6248270419 7862320900 2160990235 3043699418 4914631409 3431738143 6405462531 5209618369 0888707016 "+
		"  7683964243 7814059271 4563549061 3031072085 1038375051 0115747704 1718986106 8739696552 1267154688 9570350354 "+ // 1000
		"  0212340784 9819334321 0681701210 0562788023 5193033224 7450158539 0473041995 7777093503 6604169973 2972508868 "+
		"  7696640355 5707162268 4471625607 9882651787 1341951246 6520103059 2123667719 4325278675 3985589448 9697096409 "+
		"  7545918569 5638023637 0162112047 7427228364 8961342251 6445078182 4423529486 3637214174 0238893441 2479635743 "+
		"  7026375529 4448337998 0161254922 7850925778 2562092622 6483262779 3338656648 1627725164 0191059004 9164499828 "+
		"  9315056604 7258027786 3186415519 5653244258 6982946959 3080191529 8721172556 3475463964 4791014590 4090586298 "+ // 1500
		"  4967912874 0687050489 5858671747 9854667757 5732056812 8845920541 3340539220 0011378630 0945560688 1667400169 "+
		"  8420558040 3363795376 4520304024 3225661352 7836951177 8838638744 3966253224 9850654995 8862342818 9970773327 "+
		"  6171783928 0349465014 3455889707 1942586398 7727547109 6295374152 1115136835 0627526023 2648472870 3920764310 "+
		"  0595841166 1205452970 3023647254 9296669381 1513732275 3645098889 0313602057 2481765851 1806303644 2812314965 "+
		"  5070475102 5446501172 7211555194 8668508003 6853228183 1521960037 3562527944 9515828418 8294787610 8526398139 "+ // 2000
		"  5599006737 6482922443 7528718462 4578036192 9819713991 4756448826 2603903381 4418232625 1509748279 8777996437 "+
		"  3089970388 8677822713 8360577297 8824125611 9071766394 6507063304 5279546618 5509666618 5664709711 3444740160 "+
		"  7046262156 8071748187 7844371436 9882185596 7095910259 6862002353 7185887485 6965220005 0311734392 0732113908 "+
		"  0329363447 9727355955 2773490717 8379342163 7012050054 5132638354 4000186323 9914907054 7977805669 7853358048 "+
		"  9669062951 1943247309 9587655236 8128590413 8324116072 2602998330 5353708761 3893963917 7957454016 1372236187 "+ // 2500
		"  8936526053 8155841587 1869255386 0616477983 4025435128 4396129460 3529133259 4279490433 7299085731 5802909586 "+
		"  3138268329 1477116396 3370924003 1689458636 0606458459 2512699465 5724839186 5642097526 8508230754 4254599376 "+
		"  9170419777 8008536273 0941710163 4349076964 2372229435 2366125572 5088147792 2315197477 8060569672 5380171807 "+
		"  7636034624 5927877846 5850656050 7808442115 2969752189 0874019660 9066518035 1650179250 4619501366 5854366327 "+
		"  1254963990 8549144200 0145747608 1930221206 6024330096 4127048943 9039717719 5180699086 9986066365 8323227870 "  // 3000
		;
	//		taken from https://nerdparadise.com/math/reference/e10000
	public static String E_REF = E.replaceAll (" ", "");


	public static String PHI = // 2000 decimal digit reference
		"1.6180339887 4989484820 4586834365 6381177203 0917980576 2862135448 6227052604 6281890244 9707207204 1893911374 "+
		"  8475408807 5386891752 1266338622 2353693179 3180060766 7263544333 8908659593 9582905638 3226613199 2829026788 "+
		"  0675208766 8925017116 9620703222 1043216269 5486262963 1361443814 9758701220 3408058879 5445474924 6185695364 "+
		"  8644492410 4432077134 4947049565 8467885098 7433944221 2544877066 4780915884 6074998871 2400765217 0575179788 "+
		"  3416625624 9407589069 7040002812 1042762177 1117778053 1531714101 1704666599 1466979873 1761356006 7087480710 "+ // 500
		"  1317952368 9427521948 4353056783 0022878569 9782977834 7845878228 9110976250 0302696156 1700250464 3382437764 "+
		"  8610283831 2683303724 2926752631 1653392473 1671112115 8818638513 3162038400 5222165791 2866752946 5490681131 "+
		"  7159934323 5973494985 0904094762 1322298101 7261070596 1164562990 9816290555 2085247903 5240602017 2799747175 "+
		"  3427775927 7862561943 2082750513 1218156285 5122248093 9471234145 1702237358 0577278616 0086883829 5230459264 "+
		"  7878017889 9219902707 7690389532 1968198615 1437803149 9741106926 0886742962 2675756052 3172777520 3536139362 "+ // 1000
		"  1076738937 6455606060 5921658946 6759551900 4005559089 5022953094 2312482355 2122124154 4400647034 0565734797 "+
		"  6639723949 4994658457 8873039623 0903750339 9385621024 2369025138 6804145779 9569812244 5747178034 1731264532 "+
		"  2041639723 2134044449 4873023154 1767689375 2103068737 8803441700 9395440962 7955898678 7232095124 2689355730 "+
		"  9704509595 6844017555 1988192180 2064052905 5189349475 9260073485 2282101088 1946445442 2231889131 9294689622 "+
		"  0023014437 7026992300 7803085261 1807545192 8877050210 9684249362 7135925187 6077788466 5836150238 9134933331 "+ // 1500
		"  2231053392 3213624319 2637289106 7050339928 2265263556 2090297986 4247275977 2565508615 4875435748 2647181414 "+
		"  5127000602 3890162077 7322449943 5308899909 5016803281 1219432048 1964387675 8633147985 7191139781 5397807476 "+
		"  1507722117 5082694586 3932045652 0989698555 6781410696 8372884058 7461033781 0544439094 3683583581 3811311689 "+
		"  9385557697 5484149144 5341509129 5407005019 4775486163 0754226417 2939468036 7319805861 8339183285 9913039607 "+
		"  2014455950 4497792120 7612478564 5916160837 0594987860 0697018940 9886400764 4361709334 1727091914 3365013715 "  // 2000
		;
	//		taken from https://nerdparadise.com/math/reference/phi10000
	public static String PHI_REF = PHI.replaceAll (" ", "");


}

