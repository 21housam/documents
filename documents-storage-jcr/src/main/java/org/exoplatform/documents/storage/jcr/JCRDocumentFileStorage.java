/*
 * Copyright (C) 2021 eXo Platform SAS
 *  
 *  This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <gnu.org/licenses>.
 */
package org.exoplatform.documents.storage.jcr;

import static org.exoplatform.documents.storage.jcr.util.JCRDocumentsUtil.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.search.data.SearchResult;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.documents.model.*;
import org.exoplatform.documents.storage.DocumentFileStorage;
import org.exoplatform.documents.storage.jcr.search.DocumentSearchServiceConnector;
import org.exoplatform.documents.storage.jcr.util.NodeTypeConstants;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.spi.SpaceService;

public class JCRDocumentFileStorage implements DocumentFileStorage {

  private static final String            COLLABORATION = "collaboration";
  private final SpaceService                   spaceService;
  private final RepositoryService              repositoryService;
  private final IdentityManager                identityManager;
  private final NodeHierarchyCreator           nodeHierarchyCreator;
  private final DocumentSearchServiceConnector documentSearchServiceConnector;
  private final String                         DATE_FORMAT   = "yyyy-MM-dd";
  private final String                         SPACE_PATH_PREFIX   = "/Groups/spaces/";
  private final SimpleDateFormat               formatter     = new SimpleDateFormat(DATE_FORMAT);

  public JCRDocumentFileStorage(NodeHierarchyCreator nodeHierarchyCreator,
                                RepositoryService repositoryService,
                                DocumentSearchServiceConnector documentSearchServiceConnector,
                                IdentityManager identityManager,
                                SpaceService spaceService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
    this.repositoryService = repositoryService;
    this.nodeHierarchyCreator = nodeHierarchyCreator;
    this.documentSearchServiceConnector = documentSearchServiceConnector;
  }

  @Override
  public List<FileNode> getFilesTimeline(DocumentTimelineFilter filter,
                                         Identity aclIdentity,
                                         int offset,
                                         int limit) throws ObjectNotFoundException {
    String username = aclIdentity.getUserId();
    Long ownerId = filter.getOwnerId();
    org.exoplatform.social.core.identity.model.Identity ownerIdentity = identityManager.getIdentity(String.valueOf(ownerId));
    if (ownerIdentity == null) {
      throw new ObjectNotFoundException("Owner Identity with id : " + ownerId + " isn't found");
    }
    SessionProvider sessionProvider = getUserSessionProvider(repositoryService, aclIdentity);
    try {
      Node identityRootNode = getIdentityRootNode(spaceService, nodeHierarchyCreator, username, ownerIdentity, sessionProvider);
      if (identityRootNode == null) {
        return Collections.emptyList();
      }

      Session session = identityRootNode.getSession();
      String rootPath = identityRootNode.getPath();
      if (StringUtils.isBlank(filter.getQuery()) && BooleanUtils.isNotTrue(filter.getFavorites())) {
        String sortField = getSortField(filter, true);
        String sortDirection = getSortDirection(filter);

        String statement = getTimeLineQueryStatement(rootPath, sortField, sortDirection);
        Query jcrQuery = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
        QueryResult queryResult = jcrQuery.execute();
        NodeIterator nodeIterator = queryResult.getNodes();
        return toFileNodes(identityManager, nodeIterator, aclIdentity, offset, limit);
      } else {
        String workspace = session.getWorkspace().getName();
        String sortField = getSortField(filter, false);
        String sortDirection = getSortDirection(filter);
        Collection<SearchResult> filesSearchList = documentSearchServiceConnector.appSearch(aclIdentity,
                                                                                            workspace,
                                                                                            rootPath,
                                                                                            filter,
                                                                                            offset,
                                                                                            limit,
                                                                                            sortField,
                                                                                            sortDirection);
        return filesSearchList.stream()
                              .map(result -> toFileNode(identityManager, session, aclIdentity, result))
                              .filter(Objects::nonNull)
                              .collect(Collectors.toList());
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error retrieving User '" + username + "' parent node", e);
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();
      }
    }
  }

  @Override
  public DocumentGroupsSize getGroupDocumentsCount(DocumentTimelineFilter filter,
                                                   Identity aclIdentity) throws ObjectNotFoundException {
    String username = aclIdentity.getUserId();
    Long ownerId = filter.getOwnerId();
    org.exoplatform.social.core.identity.model.Identity ownerIdentity = identityManager.getIdentity(String.valueOf(ownerId));
    if (ownerIdentity == null) {
      throw new ObjectNotFoundException("Owner Identity with id : " + ownerId + " isn't found");
    }
    SessionProvider sessionProvider = getUserSessionProvider(repositoryService, aclIdentity);
    DocumentGroupsSize documentGroupsSize = new DocumentGroupsSize();
    try {
      Node identityRootNode = getIdentityRootNode(spaceService, nodeHierarchyCreator, username, ownerIdentity, sessionProvider);
      if (identityRootNode == null) {
        return documentGroupsSize;
      }
      Session session = identityRootNode.getSession();
      String rootPath = identityRootNode.getPath();
      if (StringUtils.isBlank(filter.getQuery())) {
        String statement = getTimeLineGroupeSizeQueryStatement(rootPath, null, new Date());
        Query jcrQuery = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
        QueryResult queryResult = jcrQuery.execute();
        documentGroupsSize.setThisDay(queryResult.getRows().getSize());

        Calendar thisWeek = GregorianCalendar.getInstance();
        thisWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Calendar thisMonth = GregorianCalendar.getInstance();
        thisMonth.set(Calendar.DAY_OF_MONTH, 0);

        Calendar thisYear = GregorianCalendar.getInstance();
        thisYear.set(Calendar.DAY_OF_YEAR, 0);

        statement = getTimeLineGroupeSizeQueryStatement(rootPath, new Date(), thisWeek.getTime());
        jcrQuery = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
        queryResult = jcrQuery.execute();
        documentGroupsSize.setThisWeek(queryResult.getRows().getSize());

        statement = getTimeLineGroupeSizeQueryStatement(rootPath, thisWeek.getTime(), thisMonth.getTime());
        jcrQuery = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
        queryResult = jcrQuery.execute();
        documentGroupsSize.setThisMonth(queryResult.getRows().getSize());

        statement = getTimeLineGroupeSizeQueryStatement(rootPath, thisMonth.getTime(), thisYear.getTime());
        jcrQuery = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
        queryResult = jcrQuery.execute();
        documentGroupsSize.setThisYear(queryResult.getRows().getSize());

        statement = getTimeLineGroupeSizeQueryStatement(rootPath, thisYear.getTime(), null);
        jcrQuery = session.getWorkspace().getQueryManager().createQuery(statement, Query.SQL);
        queryResult = jcrQuery.execute();
        documentGroupsSize.setBeforeThisYear(queryResult.getRows().getSize());
        return documentGroupsSize;

      } else {
        return documentGroupsSize;
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error retrieving User '" + username + "' parent node", e);
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();
      }
    }
  }

  @Override
  public List<AbstractNode> getFolderChildNodes(DocumentFolderFilter filter,
                                                Identity aclIdentity,
                                                int offset,
                                                int limit) throws IllegalAccessException, ObjectNotFoundException {
    String username = aclIdentity.getUserId();
    String parentFolderId = filter.getParentFolderId();
    String folderPath = filter.getFolderPath();
    SessionProvider sessionProvider = null;
    try {
      Node parent = null;
      ManageableRepository manageableRepository = repositoryService.getCurrentRepository();
      sessionProvider = getUserSessionProvider(repositoryService, aclIdentity);
      Session session = sessionProvider.getSession(COLLABORATION, manageableRepository);
      if (StringUtils.isBlank(parentFolderId)) {
        Long ownerId = filter.getOwnerId();
        org.exoplatform.social.core.identity.model.Identity ownerIdentity = identityManager.getIdentity(String.valueOf(ownerId));
        parent = getIdentityRootNode(spaceService, nodeHierarchyCreator, username, ownerIdentity, sessionProvider);
        parentFolderId = ((NodeImpl) parent).getIdentifier();
      } else {
        parent = getNodeByIdentifier(session, parentFolderId);
      }
      if(StringUtils.isNotBlank(folderPath)){
        try {
          parent = parent.getNode(java.net.URLDecoder.decode(folderPath, StandardCharsets.UTF_8.name()));
        } catch (RepositoryException repositoryException) {
          throw new ObjectNotFoundException("Folder with path : " + folderPath + " isn't found");
        }
      }
      if (parent != null) {
        if (StringUtils.isBlank(filter.getQuery()) && BooleanUtils.isNotTrue(filter.getFavorites())) {
          NodeIterator nodeIterator = parent.getNodes();
          return toNodes(identityManager, nodeIterator, aclIdentity, offset, limit);
        } else {
          String workspace = session.getWorkspace().getName();
          String sortField = getSortField(filter, false);
          String sortDirection = getSortDirection(filter);
          Collection<SearchResult> filesSearchList = documentSearchServiceConnector.appSearch(aclIdentity,
                                                                                              workspace,
                                                                                              parent.getPath(),
                                                                                              filter,
                                                                                              offset,
                                                                                              limit,
                                                                                              sortField,
                                                                                              sortDirection);
          return filesSearchList.stream()
                                .map(result -> toFileNode(identityManager, session, aclIdentity, result))
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
        }

      } else {
        throw new ObjectNotFoundException("Folder with Id : " + parentFolderId + " isn't found");
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error retrieving User '" + username + "' parent node", e);
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();
      }
    }
  }

  @Override
  public List<BreadCrumbItem> getBreadcrumb(long ownerId, String folderId, String folderPath, Identity aclIdentity) throws IllegalAccessException,
                                                                               ObjectNotFoundException {
    String username = aclIdentity.getUserId();
    SessionProvider sessionProvider = null;
    List<BreadCrumbItem> parents = new ArrayList<>();
    try {
      Node node = null;
      ManageableRepository manageableRepository = repositoryService.getCurrentRepository();
      sessionProvider = getUserSessionProvider(repositoryService, aclIdentity);
      Session session = sessionProvider.getSession(COLLABORATION, manageableRepository);
      if (StringUtils.isBlank(folderId) && ownerId > 0) {
        org.exoplatform.social.core.identity.model.Identity ownerIdentity = identityManager.getIdentity(String.valueOf(ownerId));
        node = getIdentityRootNode(spaceService, nodeHierarchyCreator, username, ownerIdentity, sessionProvider);
        folderId = ((NodeImpl) node).getIdentifier();
      } else {
        node = getNodeByIdentifier(session, folderId);
      }
      if(StringUtils.isNotBlank(folderPath)){
        try {
          node = node.getNode(java.net.URLDecoder.decode(folderPath, StandardCharsets.UTF_8.name()));
        } catch (RepositoryException repositoryException) {
          throw new ObjectNotFoundException("Folder with path : " + folderPath + " isn't found");
        }
      }
      String homePath = "";
      if (node != null) {
        parents.add(new BreadCrumbItem(((NodeImpl) node).getIdentifier(), node.getName(), node.getPath()));
        if (node.getPath().contains(SPACE_PATH_PREFIX)) {
          String[] pathParts = node.getPath().split(SPACE_PATH_PREFIX)[1].split("/");
          homePath = SPACE_PATH_PREFIX + pathParts[0] + "/" + pathParts[1];
        }
        while (node != null && !node.getPath().equals(homePath)) {
          try {
            node = node.getParent();
            if (node != null) {
              parents.add(new BreadCrumbItem(((NodeImpl) node).getIdentifier(), node.getName(), node.getPath()));
            }
          } catch (RepositoryException repositoryException) {
            node = null;
          }
        }
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error retrieving folder'" + folderId + "' breadcrumb", e);
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();
      }
    }
    return parents;
  }

  private String getTimeLineQueryStatement(String rootPath, String sortField, String sortDirection) {
    return new StringBuilder().append("SELECT * FROM ")
                              .append(NodeTypeConstants.NT_FILE)
                              .append(" WHERE jcr:path LIKE '")
                              .append(rootPath)
                              .append("/%' ORDER BY ")
                              .append(sortField)
                              .append(" ")
                              .append(sortDirection)
                              .toString();
  }

  private String getTimeLineGroupeSizeQueryStatement(String rootPath, Date before, Date after) {
    StringBuilder sb = new StringBuilder().append("SELECT * FROM ")
                                          .append(NodeTypeConstants.NT_FILE)
                                          .append(" WHERE jcr:path LIKE '")
                                          .append(rootPath)
                                          .append("/%'");
    if (before != null) {
      sb.append(" AND (")
        .append(NodeTypeConstants.EXO_DATE_MODIFIED)
        .append(" < TIMESTAMP '")
        .append(formatter.format(before))
        .append("T00:00:00.000')");
    }
    if (after != null) {
      sb.append(" AND (")
        .append(NodeTypeConstants.EXO_DATE_MODIFIED)
        .append(" > TIMESTAMP '")
        .append(formatter.format(after))
        .append("T00:00:00.000')");
    }

    return sb.toString();
  }

}
