package com.nitpeek.io.testutil;

import com.nitpeek.core.api.process.PageSource;
import com.nitpeek.core.impl.process.StringPageSource;

import java.util.List;

public final class TestFileParagraphs {
    private TestFileParagraphs() {
    }

    public static PageSource getFullContentPagesAsSeen() {
        var pages =  List.of(
                """
                        HEADER Text
                        Main Heading
                        Some simple test in a paragraph.
                        Next line.
                        Skipped 2 lines.
                        Italicized text.
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. In nec dolor finibus, porta arcu sagittis, porttitor nulla. Donec mattis, tellus in ultrices pulvinar, dui turpis hendrerit quam, sit amet sollicitudin est tortor non lectus. Nulla semper nisl libero, a ornare velit cursus ac. Mauris ut suscipit ante. Phasellus vestibulum lorem tellus. Morbi mollis tellus et libero finibus efficitur. Vestibulum non lacinia mauris, ut fringilla velit. Sed laoreet nisi non maximus molestie. Aliquam convallis, purus at molestie congue, ipsum lorem dapibus mi, vel malesuada neque ex sit amet erat. Suspendisse sit amet purus sit amet lacus sodales finibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam eu odio eu lacus venenatis pretium. Nulla tincidunt lectus risus, vel dapibus sem elementum eu. Donec vel bibendum justo. Donec tristique ut ligula vel euismod. Aliquam ligula velit, consequat vel placerat id, blandit sit amet nibh.\s
                        Vivamus nec dui placerat, cursus magna eu, ultrices velit. Mauris elit tellus, commodo vitae venenatis ac, malesuada ac ex. Vivamus cursus diam in suscipit auctor. Integer faucibus vitae augue nec pulvinar. Praesent sit amet tempor orci. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras dolor tortor, auctor a porta non, sagittis eu leo. Donec neque dui, tincidunt a malesuada in, mollis nec eros. Nullam sit amet ipsum ut mi ullamcorper elementum semper quis eros. Aenean metus nunc, lobortis quis quam ac, lacinia lobortis nisi. Nullam porta ipsum ipsum, a mattis\s
                        1Footnote 1
                        2Second footnote, which contains multiple lines
                        Second line of Footnote #2 here.
                        3This is the last one
                        Footer
                        Lines
                        3 total
                        Page 1/3""",
                """
                        HEADER Text
                        lorem dictum ac. Donec vel leo convallis, tincidunt dui sit amet, lobortis justo. Sed rhoncus vel odio ut vestibulum. Curabitur erat quam, vehicula eu nisl nec, dignissim consectetur libero. Etiam eget leo tortor.\s
                        No longer a Single-Line Page
                        Footer
                        Lines
                        3 total
                        Page 2/3""",
                """
                        HEADER Text
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)
                        Footer
                        Lines
                        3 total
                        Page 3/3"""
        );

        return new StringPageSource(pages);
    }

    public static PageSource getFullContentPagesNoSplitParagraphs() {
        var pages =  List.of(
                """
                        HEADER Text
                        Main Heading
                        Some simple test in a paragraph.
                        Next line.
                        Skipped 2 lines.
                        Italicized text.
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. In nec dolor finibus, porta arcu sagittis, porttitor nulla. Donec mattis, tellus in ultrices pulvinar, dui turpis hendrerit quam, sit amet sollicitudin est tortor non lectus. Nulla semper nisl libero, a ornare velit cursus ac. Mauris ut suscipit ante. Phasellus vestibulum lorem tellus. Morbi mollis tellus et libero finibus efficitur. Vestibulum non lacinia mauris, ut fringilla velit. Sed laoreet nisi non maximus molestie. Aliquam convallis, purus at molestie congue, ipsum lorem dapibus mi, vel malesuada neque ex sit amet erat. Suspendisse sit amet purus sit amet lacus sodales finibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam eu odio eu lacus venenatis pretium. Nulla tincidunt lectus risus, vel dapibus sem elementum eu. Donec vel bibendum justo. Donec tristique ut ligula vel euismod. Aliquam ligula velit, consequat vel placerat id, blandit sit amet nibh.\s
                        Vivamus nec dui placerat, cursus magna eu, ultrices velit. Mauris elit tellus, commodo vitae venenatis ac, malesuada ac ex. Vivamus cursus diam in suscipit auctor. Integer faucibus vitae augue nec pulvinar. Praesent sit amet tempor orci. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras dolor tortor, auctor a porta non, sagittis eu leo. Donec neque dui, tincidunt a malesuada in, mollis nec eros. Nullam sit amet ipsum ut mi ullamcorper elementum semper quis eros. Aenean metus nunc, lobortis quis quam ac, lacinia lobortis nisi. Nullam porta ipsum ipsum, a mattis lorem dictum ac. Donec vel leo convallis, tincidunt dui sit amet, lobortis justo. Sed rhoncus vel odio ut vestibulum. Curabitur erat quam, vehicula eu nisl nec, dignissim consectetur libero. Etiam eget leo tortor.\s
                        1Footnote 1
                        2Second footnote, which contains multiple lines
                        Second line of Footnote #2 here.
                        3This is the last one
                        Footer
                        Lines
                        3 total
                        Page 1/3""",
                """
                        HEADER Text
                        No longer a Single-Line Page
                        Footer
                        Lines
                        3 total
                        Page 2/3""",
                """
                        HEADER Text
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)
                        Footer
                        Lines
                        3 total
                        Page 3/3"""
        );

        return new StringPageSource(pages);
    }

    public static PageSource getBodyOnlyContent() {
        var pages =  List.of(
                """
                        Main Heading
                        Some simple test in a paragraph.
                        Next line.
                        Skipped 2 lines.
                        Italicized text.
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. In nec dolor finibus, porta arcu sagittis, porttitor nulla. Donec mattis, tellus in ultrices pulvinar, dui turpis hendrerit quam, sit amet sollicitudin est tortor non lectus. Nulla semper nisl libero, a ornare velit cursus ac. Mauris ut suscipit ante. Phasellus vestibulum lorem tellus. Morbi mollis tellus et libero finibus efficitur. Vestibulum non lacinia mauris, ut fringilla velit. Sed laoreet nisi non maximus molestie. Aliquam convallis, purus at molestie congue, ipsum lorem dapibus mi, vel malesuada neque ex sit amet erat. Suspendisse sit amet purus sit amet lacus sodales finibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam eu odio eu lacus venenatis pretium. Nulla tincidunt lectus risus, vel dapibus sem elementum eu. Donec vel bibendum justo. Donec tristique ut ligula vel euismod. Aliquam ligula velit, consequat vel placerat id, blandit sit amet nibh.\s
                        Vivamus nec dui placerat, cursus magna eu, ultrices velit. Mauris elit tellus, commodo vitae venenatis ac, malesuada ac ex. Vivamus cursus diam in suscipit auctor. Integer faucibus vitae augue nec pulvinar. Praesent sit amet tempor orci. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras dolor tortor, auctor a porta non, sagittis eu leo. Donec neque dui, tincidunt a malesuada in, mollis nec eros. Nullam sit amet ipsum ut mi ullamcorper elementum semper quis eros. Aenean metus nunc, lobortis quis quam ac, lacinia lobortis nisi. Nullam porta ipsum ipsum, a mattis\s
                        """,
                """
                        lorem dictum ac. Donec vel leo convallis, tincidunt dui sit amet, lobortis justo. Sed rhoncus vel odio ut vestibulum. Curabitur erat quam, vehicula eu nisl nec, dignissim consectetur libero. Etiam eget leo tortor.\s
                        No longer a Single-Line Page
                        """,
                """
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)
                        """
        );

        return new StringPageSource(pages);
    }

    public static PageSource getFullPagesLongParagraph() {
        var pages =  List.of(
                """
                        HEADER Text
                        Main Heading
                        Some simple test in a paragraph.
                        Next line.
                        Skipped 2 lines.
                        Italicized text.
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempor non elit vel blandit. Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris eu elit eget augue sollicitudin lobortis. Aliquam ipsum ligula, elementum sit amet orci facilisis, commodo elementum diam. Morbi vel ipsum sit amet odio aliquet dapibus at at enim. Sed velit nulla, commodo vel vulputate eget, auctor vel elit. Vestibulum aliquam, lorem sed porta aliquet, nisl elit efficitur nibh, aliquet venenatis sem massa nec massa. Curabitur eget lobortis mauris, eget ornare dui. Phasellus tincidunt elit non eros pellentesque, id pulvinar sem vestibulum. Fusce sed malesuada magna, eget fermentum risus. Maecenas dolor arcu, iaculis et quam ac, eleifend rhoncus est. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nulla in commodo augue, id tempor felis. Pellentesque pretium nulla id mauris pellentesque, eget sagittis sem dictum. In tristique orci in ex mollis iaculis. Fusce in lorem ex. Curabitur tempus tristique ligula, quis posuere felis condimentum et. Sed sagittis turpis eget scelerisque porttitor. Nunc scelerisque eget leo condimentum sollicitudin. Suspendisse nec pharetra mauris. Fusce tincidunt lorem sit amet erat condimentum commodo. Pellentesque vitae facilisis mi. Fusce facilisis feugiat congue. Integer massa turpis, egestas at pharetra eget, congue ac est. Sed iaculis dui purus, a rutrum orci vulputate vitae. Sed massa ex, rutrum eu quam at, sollicitudin faucibus nulla. Nulla convallis lectus sed metus consectetur feugiat. Ut maximus, ligula eu faucibus mattis, augue risus mollis massa, sed vehicula felis ex non felis. Etiam vitae dapibus magna. Quisque volutpat non lorem eu consectetur. In a posuere odio. Nulla vel urna ut arcu ultricies tristique. In posuere ex ac massa consectetur gravida. Integer quis imperdiet erat. Donec non nunc tempus, congue lacus sed, ullamcorper augue. Mauris turpis odio, maximus sed aliquet vel, tincidunt quis lectus. Aliquam erat volutpat. Pellentesque mollis, ipsum vel ornare aliquet, nunc magna pharetra ipsum, ac luctus sem arcu id enim. Donec tristique ante vitae massa mollis, quis ullamcorper arcu tempus. In accumsan posuere ipsum, ut venenatis nisi pharetra nec. Morbi molestie nunc at felis pharetra condimentum. Proin a nunc et turpis bibendum sagittis pretium non est. Suspendisse vel blandit felis, ut rhoncus erat. Donec lectus velit, congue eget ultrices sed, congue sit amet quam. Ut iaculis nec purus ac vehicula. Donec sodales leo sed nisl finibus faucibus in et dolor. Duis tristique augue vitae mauris blandit, quis accumsan nisl faucibus. Praesent auctor lorem sed libero sagittis ultrices. Quisque risus turpis, euismod ut dui pretium, porta commodo nulla. Sed a urna vel massa tincidunt congue. Nullam vel augue eros. Ut non feugiat augue. Sed malesuada, augue vel suscipit facilisis, ipsum sapien posuere odio, quis consequat ante arcu a sem. Mauris fringilla porta egestas. Interdum et malesuada fames ac ante ipsum primis in faucibus. Praesent ac lorem eu nibh sodales vestibulum. Fusce pretium lorem non nulla egestas, at porttitor felis porttitor. Donec sit amet nunc lobortis, feugiat nunc et, aliquam mauris. Aliquam vulputate vitae elit sit amet scelerisque. Maecenas suscipit, sem nec pulvinar tristique, erat tellus accumsan elit, id aliquet elit diam a erat. Duis aliquet tincidunt orci, ac laoreet nulla posuere sit amet. Quisque ac urna ullamcorper nisl dignissim dapibus nec a mauris. Donec vehicula nec libero a maximus. Nunc et velit tempor, pharetra lorem sed, congue tortor. Aenean ex arcu, rutrum vitae convallis non, gravida quis eros. Integer hendrerit tortor sit amet laoreet condimentum. Vivamus gravida pulvinar quam sit amet lacinia. Suspendisse libero urna, varius sed metus pulvinar, consectetur ullamcorper ligula. Quisque porta elit vitae pulvinar ornare. Curabitur porttitor magna mi, non congue justo dapibus in. Mauris nibh leo, porttitor et turpis vel, fermentum fermentum dui. Nam in augue eget risus pharetra commodo et a ligula. Duis rhoncus, neque non egestas convallis, nisi nulla volutpat massa, vel posuere velit risus id neque. Praesent malesuada et eros in porta. Cras fermentum neque at molestie aliquam. Phasellus nec faucibus dolor, vel suscipit massa. Sed eu velit sit amet ex dictum efficitur. Maecenas sed ante elementum, auctor tellus ut, suscipit sem. Quisque elementum dolor quam, in viverra tellus pharetra sit amet. Sed fermentum suscipit est a cursus. Nunc in sagittis ipsum. Nullam sed est in dolor porttitor bibendum et a ligula. Nam at ultrices massa, eu accumsan dui. Duis orci felis, bibendum non tellus et, ornare bibendum libero. Curabitur erat tellus, tempus ac feugiat dignissim, luctus vel nulla. Duis ullamcorper faucibus ligula, dapibus commodo arcu eleifend sed. Integer faucibus justo aliquam nunc euismod mollis. Mauris id nisi sollicitudin, pharetra nulla id, elementum dolor. Sed blandit ipsum nec semper sodales. Nullam et felis nec mauris dictum convallis at quis enim. Maecenas tortor elit, pretium at congue eu, blandit id nisi. Phasellus velit sapien, sagittis ac iaculis non, vehicula non nibh. Suspendisse eu tincidunt purus. Curabitur maximus, mi nec facilisis rhoncus, tortor eros mollis turpis, quis fermentum elit urna id metus. Nam et ex molestie, mattis purus eget, fringilla nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Maecenas iaculis nulla eu condimentum tristique. Curabitur massa augue, rhoncus vitae pellentesque non, ultricies ac nisl. Cras eget consectetur neque, rhoncus elementum dui. Morbi non lacus risus. Suspendisse libero neque, tempus vitae turpis rutrum, ornare auctor velit. Maecenas convallis purus eu augue sollicitudin consectetur. Mauris aliquam, tortor sed pulvinar fermentum, arcu nisi tempus leo, sit amet varius felis tortor non massa. Cras dignissim egestas purus eget finibus. Aliquam ullamcorper nunc sem, et dictum mauris faucibus vitae. Aenean sed tellus at nibh rhoncus efficitur. Sed vulputate neque ac risus faucibus fringilla. Sed eu vestibulum lacus, vitae pharetra nunc. Fusce non luctus felis, nec consequat lectus. Nullam blandit diam turpis, vel iaculis neque malesuada et. Phasellus maximus hendrerit risus, et pulvinar nunc scelerisque sed. Etiam a malesuada massa. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur maximus ligula eu nunc aliquet porta. Pellentesque at viverra odio. Cras a vulputate enim. In fermentum nibh in tincidunt interdum. Proin hendrerit rutrum urna, sed laoreet dolor rhoncus vel. Curabitur dictum sem maximus est ultrices ornare. Donec vel vulputate mi. Nulla ornare ultrices lectus at aliquet. Donec leo dui, finibus eu nibh vitae, auctor laoreet metus. In vel mauris at orci convallis pharetra. Phasellus nec erat nisl. Suspendisse potenti. Phasellus dignissim tristique viverra. Etiam sit amet bibendum lorem. Vivamus non libero at lorem scelerisque aliquam vitae bibendum justo. Vivamus sed dapibus orci, id vestibulum lacus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Donec iaculis vehicula rutrum. Nulla facilisi. Vivamus sodales sapien tortor, at mattis elit finibus ut. Integer ultrices at mi quis pharetra. Quisque mattis dapibus ipsum, sed ultrices orci sollicitudin non. Nullam eget dictum arcu. Aenean nec erat at metus maximus pretium. Nunc rhoncus tincidunt metus ac rhoncus. Integer ac accumsan urna. Morbi fermentum, ligula eget vestibulum dictum, lacus quam volutpat lorem, nec iaculis lacus arcu non mauris. Nullam euismod in velit ut tincidunt. Morbi aliquet a lacus sed vestibulum. Phasellus a libero vitae nisl feugiat ultrices nec vitae dolor. Nam tincidunt quam non tortor fermentum tempor. Cras et sem dui. Phasellus suscipit mollis nisi, blandit dictum tellus efficitur eu. Phasellus faucibus lorem ac elementum pulvinar. Vivamus condimentum velit in elementum venenatis. Nam velit lorem, pharetra at enim ac, dictum consectetur nibh. Sed ornare, nibh nec dapibus ultricies, enim diam pretium sapien, nec varius mi magna id sem. Praesent non faucibus lacus. Vivamus eget mauris a nulla tristique blandit vel id erat. Mauris accumsan mauris vitae vestibulum vestibulum. Ut rhoncus tristique leo, sed tempus neque porta a. Vestibulum tincidunt purus eget elit consectetur, in auctor tellus lobortis. Sed quis sapien interdum, pharetra libero vehicula, egestas eros. Aenean viverra nibh pretium tempus congue. Donec consectetur euismod quam, et pulvinar augue lacinia sed. Nullam vitae dolor pellentesque, pulvinar mi nec, dapibus odio. Nulla volutpat erat enim, vitae imperdiet justo vestibulum in. Duis pretium nibh ex, at sagittis dolor aliquam a. Praesent aliquam dolor a justo consequat, sit amet dapibus massa tincidunt. Maecenas condimentum erat eget malesuada fringilla. Phasellus luctus ex vitae eleifend facilisis. Cras tincidunt velit nunc, quis convallis enim convallis quis. Vivamus ut ultrices elit. Mauris luctus ullamcorper erat, non malesuada erat consectetur nec. In hac habitasse platea dictumst. Mauris enim magna, bibendum non commodo nec, viverra a orci. Sed scelerisque ex vitae purus vulputate cursus. Maecenas at velit laoreet, luctus sem quis, auctor metus. Aenean eu urna scelerisque, tincidunt tellus eu, convallis lectus. Donec convallis risus nibh, vel pulvinar metus eleifend et. Morbi aliquet tellus eu lacus fermentum, a hendrerit nibh viverra. Mauris quis magna tincidunt, dictum dui eget, fringilla nunc. Maecenas rutrum in neque accumsan viverra. Quisque et ultrices diam. Proin congue et ligula id facilisis. Nam odio turpis, sollicitudin at magna quis, aliquet molestie dolor. Etiam a nulla eu felis laoreet hendrerit vitae et nulla. Cras at aliquet sem, et scelerisque metus. Cras interdum sagittis viverra. Vivamus velit purus, dictum non tincidunt sed, malesuada vitae sem. Nullam eleifend felis tincidunt leo sollicitudin posuere ut id nisl. Praesent cursus nisl et felis suscipit, nec dignissim sem fringilla. Quisque id purus ex. Maecenas vitae cursus justo, in dapibus neque. Suspendisse potenti. Ut vel ligula vulputate, rhoncus ante rhoncus, lacinia nisl. Donec dictum est at quam imperdiet pellentesque. Fusce semper malesuada nibh non vehicula. Ut sollicitudin ligula in ornare accumsan. Pellentesque consectetur egestas porttitor. Maecenas vulputate nunc a enim facilisis, eget ultrices elit vestibulum. Suspendisse augue lectus, fringilla et ante euismod, mattis tempus nisl. Phasellus dictum velit in tempor lobortis. Aenean viverra felis ac arcu accumsan auctor. Duis ultricies turpis id lacinia convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed lacinia ex at ante egestas tempor. Quisque mollis, sapien vitae pharetra sollicitudin, nisl ipsum tempus libero, sit amet bibendum ante tellus id elit. Ut non eleifend sapien, ut elementum ex. Aliquam velit nulla, faucibus quis arcu sit amet, sollicitudin ornare velit. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque id elit eros. Nunc hendrerit id felis quis porttitor. Sed sit amet odio ac magna tristique sodales eget ut urna. Phasellus porttitor auctor tempus. Maecenas sagittis nibh congue, finibus mi eu, commodo tortor. Aliquam vulputate elementum lacus. Vivamus gravida leo eget tortor suscipit rhoncus. Duis eu dolor tellus. Aenean at lacus ac odio porta porttitor non pellentesque ante. Mauris mollis, diam auctor sollicitudin dictum, ex nulla rutrum nulla, non consequat tortor orci in eros. Aenean fermentum, justo sit amet vulputate mattis, odio urna suscipit metus, vel semper ex felis sed velit. Maecenas ut dignissim felis, eget elementum nulla. Nam bibendum nisl arcu, nec vestibulum est aliquam vitae. Donec egestas, sapien sit amet rhoncus sollicitudin, neque massa auctor nisl, non faucibus massa ante et dui. Integer maximus lacinia quam, sed gravida diam condimentum in. Mauris id sem nisl. Aenean hendrerit hendrerit enim, in interdum justo fringilla eget. Nunc maximus a odio sed tristique. Aenean vitae sem pretium, fringilla sapien sit amet, consequat velit. Phasellus quis lacinia leo. Nulla tristique sapien in aliquam posuere. Donec non libero augue. Donec bibendum viverra felis at pulvinar. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus scelerisque tincidunt odio, quis mollis elit posuere id. Pellentesque mattis et elit sed consectetur. Integer interdum nunc et erat viverra, in laoreet dolor mattis. Donec vestibulum feugiat tortor, in laoreet mi pretium ac. Sed eu iaculis eros, vitae consectetur lacus. Mauris vestibulum suscipit vehicula. Aliquam erat volutpat. Nulla facilisi. Phasellus et enim quis nisl venenatis vehicula ac efficitur sapien. Sed consequat enim odio, et elementum sapien placerat ut. Sed lobortis felis non urna egestas bibendum. Phasellus eleifend quis ligula vel ultrices. Praesent lacus tortor, rhoncus eget congue placerat, fringilla vel augue. Mauris et risus sagittis, maximus est eget, bibendum massa. Ut at odio rutrum, pharetra erat vel, semper urna. Proin vel dignissim nunc. Mauris eros sem, scelerisque at euismod id, cursus at arcu.
                        1Footnote 1
                        2Second footnote, which contains multiple lines
                        Second line of Footnote #2 here.
                        3This is the last one
                        Footer
                        Lines
                        3 total
                        Page 1/5""",
                """
                        HEADER Text
                        Footer
                        Lines
                        3 total
                        Page 2/5""",
                """
                        HEADER Text
                        Footer
                        Lines
                        3 total
                        Page 3/5""",
                """
                        HEADER Text
                        No longer a Single-Line Page
                        Footer
                        Lines
                        3 total
                        Page 4/5""",
                """
                        HEADER Text
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)
                        Footer
                        Lines
                        3 total
                        Page 5/5"""
        );

        return new StringPageSource(pages);
    }

    public static PageSource getBodyOnlyPagesLongParagraph() {
        var pages =  List.of(
                """
                        Main Heading    
                        Some simple test in a paragraph.
                        Next line.            
                        Skipped 2 lines.
                        Italicized text.          
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempor non elit vel blandit. Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris eu elit eget augue sollicitudin lobortis. Aliquam ipsum ligula, elementum sit amet orci facilisis, commodo elementum diam. Morbi vel ipsum sit amet odio aliquet dapibus at at enim. Sed velit nulla, commodo vel vulputate eget, auctor vel elit. Vestibulum aliquam, lorem sed porta aliquet, nisl elit efficitur nibh, aliquet venenatis sem massa nec massa. Curabitur eget lobortis mauris, eget ornare dui. Phasellus tincidunt elit non eros pellentesque, id pulvinar sem vestibulum. Fusce sed malesuada magna, eget fermentum risus. Maecenas dolor arcu, iaculis et quam ac, eleifend rhoncus est. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nulla in commodo augue, id tempor felis. Pellentesque pretium nulla id mauris pellentesque, eget sagittis sem dictum. In tristique orci in ex mollis iaculis. Fusce in lorem ex. Curabitur tempus tristique ligula, quis posuere felis condimentum et. Sed sagittis turpis eget scelerisque porttitor. Nunc scelerisque eget leo condimentum sollicitudin. Suspendisse nec pharetra mauris. Fusce tincidunt lorem sit amet erat condimentum commodo. Pellentesque vitae facilisis mi. Fusce facilisis feugiat congue. Integer massa turpis, egestas at pharetra eget, congue ac est. Sed iaculis dui purus, a rutrum orci vulputate vitae. Sed massa ex, rutrum eu quam at, sollicitudin faucibus nulla. Nulla convallis lectus sed metus consectetur feugiat. Ut maximus, ligula eu faucibus mattis, augue risus mollis massa, sed vehicula felis ex non felis. Etiam vitae dapibus magna. Quisque volutpat non lorem eu consectetur. In a posuere odio. Nulla vel urna ut arcu ultricies tristique. In posuere ex ac massa consectetur gravida. Integer quis imperdiet erat. Donec non nunc tempus, congue lacus sed, ullamcorper augue. Mauris turpis odio, maximus sed aliquet vel, tincidunt quis lectus. Aliquam erat volutpat. Pellentesque mollis, ipsum vel ornare aliquet, nunc magna pharetra ipsum, ac luctus sem arcu id enim. Donec tristique ante vitae massa mollis, quis ullamcorper arcu tempus. In accumsan posuere ipsum, ut venenatis nisi pharetra nec. Morbi molestie nunc at felis pharetra condimentum. Proin a nunc et turpis bibendum sagittis pretium non est. Suspendisse vel blandit felis, ut rhoncus erat. Donec lectus velit, congue eget ultrices sed, congue sit amet quam. Ut iaculis nec purus ac vehicula. Donec sodales leo sed nisl finibus faucibus in et dolor. Duis tristique augue vitae mauris blandit, quis accumsan nisl faucibus. Praesent auctor lorem sed libero sagittis ultrices. Quisque risus turpis, euismod ut dui pretium, porta commodo nulla. Sed a urna vel massa tincidunt congue. Nullam vel augue eros. Ut non feugiat augue. Sed malesuada, augue vel suscipit facilisis, ipsum sapien posuere odio, quis consequat ante arcu a sem. Mauris fringilla porta egestas. Interdum et malesuada fames ac ante ipsum primis in faucibus. Praesent ac lorem eu nibh sodales vestibulum. Fusce pretium lorem non nulla egestas, at porttitor felis porttitor. Donec sit amet nunc lobortis, feugiat nunc et, aliquam mauris. Aliquam vulputate vitae elit sit amet scelerisque. Maecenas suscipit, sem nec pulvinar tristique, erat tellus accumsan elit, id aliquet elit diam a erat. Duis aliquet tincidunt orci, ac laoreet nulla posuere sit amet. Quisque ac urna ullamcorper nisl dignissim dapibus nec a mauris. Donec vehicula nec libero a maximus. Nunc et velit tempor, pharetra lorem sed, congue tortor. Aenean ex arcu, rutrum vitae convallis non, gravida quis eros. Integer hendrerit tortor sit amet laoreet condimentum. Vivamus gravida pulvinar quam sit amet lacinia. Suspendisse libero urna, varius sed metus pulvinar, consectetur ullamcorper ligula. Quisque porta elit vitae pulvinar ornare. Curabitur porttitor magna mi, non congue justo dapibus in. Mauris nibh leo, porttitor et turpis vel, fermentum fermentum dui. Nam in augue eget risus pharetra commodo et a ligula. Duis rhoncus, neque non egestas convallis, nisi nulla volutpat massa, vel posuere velit risus id neque. Praesent malesuada et eros in porta. Cras fermentum neque at molestie aliquam. Phasellus nec faucibus dolor, vel suscipit massa. Sed eu velit sit amet ex dictum efficitur. Maecenas sed ante elementum, auctor tellus ut, suscipit sem. Quisque elementum dolor quam, in viverra tellus pharetra sit amet. Sed fermentum suscipit est a cursus. Nunc in sagittis ipsum. Nullam sed est in dolor porttitor bibendum et a ligula. Nam at ultrices massa, eu accumsan dui. Duis orci felis, bibendum non tellus et, ornare bibendum libero. Curabitur erat tellus, tempus ac feugiat dignissim, luctus vel nulla. Duis ullamcorper faucibus ligula, dapibus commodo arcu eleifend sed. Integer faucibus justo aliquam nunc euismod mollis. Mauris id nisi sollicitudin, pharetra nulla id, elementum dolor. Sed blandit ipsum nec semper sodales. Nullam et felis nec mauris dictum convallis at quis enim. Maecenas tortor elit, pretium at congue eu, blandit id nisi. Phasellus velit sapien, sagittis ac iaculis non, vehicula non nibh. Suspendisse eu tincidunt purus. Curabitur maximus, mi nec facilisis rhoncus, tortor eros mollis turpis, quis fermentum elit urna id metus. Nam et ex molestie, mattis purus eget, fringilla nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Maecenas iaculis nulla eu condimentum tristique. Curabitur massa augue, rhoncus vitae pellentesque non, ultricies ac nisl. Cras eget consectetur neque, rhoncus elementum dui. Morbi non lacus risus. Suspendisse libero neque, tempus vitae turpis rutrum, ornare auctor velit. Maecenas convallis purus eu augue sollicitudin consectetur. Mauris aliquam, tortor sed pulvinar fermentum, arcu nisi tempus leo, sit amet varius felis tortor non massa. Cras dignissim egestas purus eget finibus. Aliquam ullamcorper nunc sem, et dictum mauris faucibus vitae. Aenean sed tellus at nibh rhoncus efficitur. Sed vulputate neque ac risus faucibus fringilla. Sed eu vestibulum lacus, vitae pharetra nunc. Fusce non luctus felis, nec consequat lectus. Nullam blandit diam turpis, vel iaculis neque malesuada et. Phasellus maximus hendrerit risus, et pulvinar nunc scelerisque sed. Etiam a malesuada massa. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur maximus ligula eu nunc aliquet porta. Pellentesque at viverra odio. Cras a vulputate enim. In fermentum nibh in tincidunt interdum. Proin hendrerit rutrum urna, sed laoreet dolor rhoncus vel. Curabitur dictum sem maximus est ultrices ornare. Donec vel vulputate mi. Nulla ornare ultrices lectus at aliquet. Donec leo dui, finibus eu nibh vitae, auctor laoreet metus. In vel mauris at orci convallis pharetra. Phasellus nec erat nisl. Suspendisse potenti. Phasellus dignissim tristique viverra. Etiam sit amet bibendum lorem. Vivamus non libero at lorem scelerisque aliquam vitae bibendum justo. Vivamus sed dapibus orci, id vestibulum lacus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Donec iaculis vehicula rutrum. Nulla facilisi. Vivamus sodales sapien tortor, at mattis elit finibus ut. Integer ultrices at mi quis pharetra. Quisque mattis dapibus ipsum, sed ultrices orci sollicitudin non. Nullam eget dictum arcu. Aenean nec erat at metus maximus pretium. Nunc rhoncus tincidunt metus ac rhoncus. Integer ac accumsan urna. Morbi fermentum, ligula eget vestibulum dictum, lacus quam volutpat lorem, nec iaculis lacus arcu non mauris. Nullam euismod in velit ut tincidunt. Morbi aliquet a lacus sed vestibulum. Phasellus a libero vitae nisl feugiat ultrices nec vitae dolor. Nam tincidunt quam non tortor fermentum tempor. Cras et sem dui. Phasellus suscipit mollis nisi, blandit dictum tellus efficitur eu. Phasellus faucibus lorem ac elementum pulvinar. Vivamus condimentum velit in elementum venenatis. Nam velit lorem, pharetra at enim ac, dictum consectetur nibh. Sed ornare, nibh nec dapibus ultricies, enim diam pretium sapien, nec varius mi magna id sem. Praesent non faucibus lacus. Vivamus eget mauris a nulla tristique blandit vel id erat. Mauris accumsan mauris vitae vestibulum vestibulum. Ut rhoncus tristique leo, sed tempus neque porta a. Vestibulum tincidunt purus eget elit consectetur, in auctor tellus lobortis. Sed quis sapien interdum, pharetra libero vehicula, egestas eros. Aenean viverra nibh pretium tempus congue. Donec consectetur euismod quam, et pulvinar augue lacinia sed. Nullam vitae dolor pellentesque, pulvinar mi nec, dapibus odio. Nulla volutpat erat enim, vitae imperdiet justo vestibulum in. Duis pretium nibh ex, at sagittis dolor aliquam a. Praesent aliquam dolor a justo consequat, sit amet dapibus massa tincidunt. Maecenas condimentum erat eget malesuada fringilla. Phasellus luctus ex vitae eleifend facilisis. Cras tincidunt velit nunc, quis convallis enim convallis quis. Vivamus ut ultrices elit. Mauris luctus ullamcorper erat, non malesuada erat consectetur nec. In hac habitasse platea dictumst. Mauris enim magna, bibendum non commodo nec, viverra a orci. Sed scelerisque ex vitae purus vulputate cursus. Maecenas at velit laoreet, luctus sem quis, auctor metus. Aenean eu urna scelerisque, tincidunt tellus eu, convallis lectus. Donec convallis risus nibh, vel pulvinar metus eleifend et. Morbi aliquet tellus eu lacus fermentum, a hendrerit nibh viverra. Mauris quis magna tincidunt, dictum dui eget, fringilla nunc. Maecenas rutrum in neque accumsan viverra. Quisque et ultrices diam. Proin congue et ligula id facilisis. Nam odio turpis, sollicitudin at magna quis, aliquet molestie dolor. Etiam a nulla eu felis laoreet hendrerit vitae et nulla. Cras at aliquet sem, et scelerisque metus. Cras interdum sagittis viverra. Vivamus velit purus, dictum non tincidunt sed, malesuada vitae sem. Nullam eleifend felis tincidunt leo sollicitudin posuere ut id nisl. Praesent cursus nisl et felis suscipit, nec dignissim sem fringilla. Quisque id purus ex. Maecenas vitae cursus justo, in dapibus neque. Suspendisse potenti. Ut vel ligula vulputate, rhoncus ante rhoncus, lacinia nisl. Donec dictum est at quam imperdiet pellentesque. Fusce semper malesuada nibh non vehicula. Ut sollicitudin ligula in ornare accumsan. Pellentesque consectetur egestas porttitor. Maecenas vulputate nunc a enim facilisis, eget ultrices elit vestibulum. Suspendisse augue lectus, fringilla et ante euismod, mattis tempus nisl. Phasellus dictum velit in tempor lobortis. Aenean viverra felis ac arcu accumsan auctor. Duis ultricies turpis id lacinia convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed lacinia ex at ante egestas tempor. Quisque mollis, sapien vitae pharetra sollicitudin, nisl ipsum tempus libero, sit amet bibendum ante tellus id elit. Ut non eleifend sapien, ut elementum ex. Aliquam velit nulla, faucibus quis arcu sit amet, sollicitudin ornare velit. Interdum et malesuada fames ac ante ipsum primis in faucibus. Pellentesque id elit eros. Nunc hendrerit id felis quis porttitor. Sed sit amet odio ac magna tristique sodales eget ut urna. Phasellus porttitor auctor tempus. Maecenas sagittis nibh congue, finibus mi eu, commodo tortor. Aliquam vulputate elementum lacus. Vivamus gravida leo eget tortor suscipit rhoncus. Duis eu dolor tellus. Aenean at lacus ac odio porta porttitor non pellentesque ante. Mauris mollis, diam auctor sollicitudin dictum, ex nulla rutrum nulla, non consequat tortor orci in eros. Aenean fermentum, justo sit amet vulputate mattis, odio urna suscipit metus, vel semper ex felis sed velit. Maecenas ut dignissim felis, eget elementum nulla. Nam bibendum nisl arcu, nec vestibulum est aliquam vitae. Donec egestas, sapien sit amet rhoncus sollicitudin, neque massa auctor nisl, non faucibus massa ante et dui. Integer maximus lacinia quam, sed gravida diam condimentum in. Mauris id sem nisl. Aenean hendrerit hendrerit enim, in interdum justo fringilla eget. Nunc maximus a odio sed tristique. Aenean vitae sem pretium, fringilla sapien sit amet, consequat velit. Phasellus quis lacinia leo. Nulla tristique sapien in aliquam posuere. Donec non libero augue. Donec bibendum viverra felis at pulvinar. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus scelerisque tincidunt odio, quis mollis elit posuere id. Pellentesque mattis et elit sed consectetur. Integer interdum nunc et erat viverra, in laoreet dolor mattis. Donec vestibulum feugiat tortor, in laoreet mi pretium ac. Sed eu iaculis eros, vitae consectetur lacus. Mauris vestibulum suscipit vehicula. Aliquam erat volutpat. Nulla facilisi. Phasellus et enim quis nisl venenatis vehicula ac efficitur sapien. Sed consequat enim odio, et elementum sapien placerat ut. Sed lobortis felis non urna egestas bibendum. Phasellus eleifend quis ligula vel ultrices. Praesent lacus tortor, rhoncus eget congue placerat, fringilla vel augue. Mauris et risus sagittis, maximus est eget, bibendum massa. Ut at odio rutrum, pharetra erat vel, semper urna. Proin vel dignissim nunc. Mauris eros sem, scelerisque at euismod id, cursus at arcu.            
                        """,
                "", // while these 2 pages have empty bodies, they still contain header, footer, and footnotes
                "", // while these 2 pages have empty bodies, they still contain header, footer, and footnotes
                """
                        No longer a Single-Line Page
                        """,
                """
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)          
                        """
        );

        return new StringPageSource(pages);
    }

    public static PageSource getFootnotesOnlyContent() {
        var pages =  List.of(
                """
                        1Footnote 1
                        2Second footnote, which contains multiple lines
                        Second line of Footnote #2 here.
                        3This is the last one
                        """,
                "",
                ""
        );

        return new StringPageSource(pages);
    }
}
