/*
 *  WikipediaCleaner: A tool to help on Wikipedia maintenance tasks.
 *  Copyright (C) 2012  Nicolas Vervelle
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wikipediacleaner.api.request.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.wikipediacleaner.api.APIException;
import org.wikipediacleaner.api.constants.EnumWikipedia;
import org.wikipediacleaner.api.data.DataManager;
import org.wikipediacleaner.api.data.Page;
import org.wikipediacleaner.api.request.ApiLinksResult;
import org.wikipediacleaner.api.request.ApiRequest;
import org.wikipediacleaner.api.request.ConnectionInformation;


/**
 * MediaWiki API XML links results.
 */
public class ApiXmlLinksResult extends ApiXmlPropertiesResult implements ApiLinksResult {

  /**
   * @param wiki Wiki on which requests are made.
   * @param httpClient HTTP client for making requests.
   * @param connection Connection information.
   */
  public ApiXmlLinksResult(
      EnumWikipedia wiki,
      HttpClient httpClient,
      ConnectionInformation connection) {
    super(wiki, httpClient, connection);
  }

  /**
   * Execute links request.
   * 
   * @param properties Properties defining request.
   * @param lists Map of lists to be filled with links.
   * @param normalization Map containing information about title normalization (From => To).
   * @return True if request should be continued.
   * @throws APIException
   */
  public boolean executeLinks(
      Map<String, String> properties,
      Map<String, List<Page>> lists,
      Map<String, String> normalization) throws APIException {
    try {
      Element root = getRoot(properties, ApiRequest.MAX_ATTEMPTS);

      // Retrieve normalization information
      retrieveNormalization(root, normalization);

      // Retrieve back links
      XPath xpaPages = XPath.newInstance("/api/query/pages/page");
      List listPages = xpaPages.selectNodes(root);
      Iterator itPage = listPages.iterator();
      XPath xpaNs = XPath.newInstance("./@ns");
      XPath xpaTitle = XPath.newInstance("./@title");
      XPath xpaLinks = XPath.newInstance("links/pl");
      while (itPage.hasNext()) {
        Element pageNode = (Element) itPage.next();
        String pageTitle = xpaTitle.valueOf(pageNode);
        List<Page> links = lists.get(pageTitle);
        if (links == null) {
          links = new ArrayList<Page>();
          lists.put(pageTitle, links);
        }
        List listLinks = xpaLinks.selectNodes(pageNode);
        Iterator itLinks = listLinks.iterator();
        while (itLinks.hasNext()) {
          Element linkNode = (Element) itLinks.next();
          Page link = DataManager.getPage(
              getWiki(), xpaTitle.valueOf(linkNode), null, null);
          link.setNamespace(xpaNs.valueOf(linkNode));
          links.add(link);
        }
      }

      // Retrieve continue
      return shouldContinue(
          root, "/api/query-continue/links",
          properties);
    } catch (JDOMException e) {
      log.error("Error loading links", e);
      throw new APIException("Error parsing XML", e);
    }
  }
}