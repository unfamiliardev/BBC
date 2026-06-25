/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.data

object DefaultPlaylist {

    const val BUILTIN_URL = "bbc://builtin"
    const val BUILTIN_NAME = "Built-in Channels"

    val content: String = """
#EXTM3U
#EXTINF:-1 tvg-name="BBC News" tvg-id="BBCNews.uk" tvg-logo="https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/BBC_News_2022_%28Alt%29.svg/240px-BBC_News_2022_%28Alt%29.svg.png" group-title="News",BBC News
http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_news24.m3u8
#EXTINF:-1 tvg-name="BBC One" tvg-id="BBCOne.uk" tvg-logo="https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/BBC_One_logo_%282021%29.svg/240px-BBC_One_logo_%282021%29.svg.png" group-title="Entertainment",BBC One
http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_one_hd.m3u8
#EXTINF:-1 tvg-name="BBC Two" tvg-id="BBCTwo.uk" tvg-logo="https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/BBC_Two_HD.svg/240px-BBC_Two_HD.svg.png" group-title="Entertainment",BBC Two
http://a.files.bbci.co.uk/media/live/manifesto/audio_video/simulcast/hls/uk/abr_hdtv/ak/bbc_two_england.m3u8
#EXTINF:-1 tvg-name="Red Bull TV" tvg-id="RedBullTV" tvg-logo="https://img.redbull.com/images/c_crop,w_1904,h_1904,x_0,y_0,f_auto,q_auto/c_scale,w_120/redbullcom/2019/07/26/fbb64024-fe6c-4a17-8a50-f8c5c1a24a2e/redbull-tv-icon" group-title="Sports",Red Bull TV
https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8
#EXTINF:-1 tvg-name="DW English" tvg-id="DWEnglish" tvg-logo="https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Deutsche_Welle_symbol_2012.svg/240px-Deutsche_Welle_symbol_2012.svg.png" group-title="News",DW English
https://dwamdstream102.akamaized.net/hls/live/2015525/dwstream102/index.m3u8
#EXTINF:-1 tvg-name="NASA TV" tvg-id="NASATV" tvg-logo="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/240px-NASA_logo.svg.png" group-title="Science",NASA TV
https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8
    """.trimIndent()
}
