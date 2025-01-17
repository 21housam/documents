/*
 * Copyright (C) 2024 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import DocumentsMain from './components/DocumentsMain.vue';
import DocumentsHeader from './components/header/DocumentsHeader.vue';
import DocumentsHeaderLeft from './components/header/DocumentsHeaderLeft.vue';
import DocumentsAddNewFile from './components/header/actions/DocumentsAddNewFile.vue';
import DocumentsBody from './components/body/DocumentsBody.vue';
import DocumentsNoBody from './components/body/DocumentsNoBody.vue';
import DocumentsNoBodyFolder from './components/body/DocumentsNoBodyFolder.vue';
import DocumentsNoResultBody from './components/body/DocumentsNoResultBody.vue';
import DocumentsTimelineView from './components/body/views/DocumentsTimelineView.vue';
import DocumentsFolderView from './components/body/views/DocumentsFolderView.vue';
import DocumentsTimelineGroupHeader from './components/body/views/DocumentsTimelineGroupHeader.vue';
import DocumentsTableCell from './components/body/table/DocumentsTableCell.vue';
import DocumentsLastUpdatedCell from './components/body/table/cells/DocumentsLastUpdatedCell.vue';
import DocumentsVisibilityCell from './components/body/table/cells/DocumentsVisibilityCell.vue';
import DocumentsFileSizeCell from './components/body/table/cells/DocumentsFileSizeCell.vue';
import DocumentsLastActivityCell from './components/body/table/cells/DocumentsLastActivityCell.vue';
import DocumentsFavoriteCell from './components/body/table/cells/DocumentsFavoriteCell.vue';
import DocumentsFavoriteAction from './components/body/table/action/DocumentFavoriteAction.vue';
import DocumentsFileNameCell from './components/body/table/cells/DocumentsFileNameCell.vue';
import DocumentsFileEditNameCell from './components/body/table/cells/DocumentsFileEditNameCell.vue';
import SelectPeriod from './components/header/filters/SelectPeriod.vue';
import DocumentActionMenu from './components/body/table/DocumentActionMenu.vue';
import DocumentActionMenuMobile from './components/body/table/DocumentActionMenuMobile.vue';
import DocumentInfoDrawer from './components/body/table/DocumentInfoDrawer.vue';
import DocumentDownloadDrawer from './components/body/table/DocumentDownloadDrawer.vue';
import DocumentsTreeSelectorDrawer from './components/body/table/DocumentsTreeSelectorDrawer.vue';
import DocumentsMoveSpaces from './components/body/table/DocumentsMoveSpaces.vue';
import DocumentsVisibilityDrawer from './components/body/table/DocumentsVisibilityDrawer.vue';
import DocumentsAllUsersVisibilityDrawer from './components/body/table/DocumentsAllUsersVisibilityDrawer.vue';
import DocumentsVisibilityCollaborators from './components/body/table/DocumentsVisibilityCollaborators.vue';
import DocumentsVisibilityMenu from './components/body/table/DocumentsVisibilityMenu.vue';
import EditMenuAction from './components/body/actions/EditMenuAction.vue';
import DownloadMenuAction from './components/body/actions/DownloadMenuAction.vue';
import OpenLocationMenuAction from './components/body/actions/OpenLocationMenuAction.vue';
import OpenReadOnlyMenuAction from './components/body/actions/OpenReadOnlyMenuAction.vue';
import MoveMenuAction from './components/body/actions/MoveMenuAction.vue';
import DuplicateMenuAction from './components/body/actions/DuplicateMenuAction.vue';
import VisibilityMenuAction from './components/body/actions/VisibilityMenuAction.vue';
import FavoriteMenuAction from './components/body/actions/FavoriteMenuAction.vue';
import RenameMenuAction from './components/body/actions/RenameMenuAction.vue';
import CopyLinkMenuAction from './components/body/actions/CopyLinkMenuAction.vue';
import DocumentsBreadcrumb from './components/body/views/DocumentsBreadcrumb.vue';
import FolderTreeViewDrawer from './components/body/views/FolderTreeViewDrawer.vue';
import DetailsMenuAction from './components/body/actions/DetailsMenuAction.vue';
import DeleteMenuAction from './components/body/actions/DeleteMenuAction.vue';
import ShortcutMenuAction from './components/body/actions/ShortcutMenuAction.vue';
import DocumentsInfoDetailsCell from './components/body/table/cells/DocumentsInfoDetailsCell.vue';
import DocumentAddNewMobile from './components/header/actions/DocumentAddNewMobile.vue';
import DocumentAdvancedFilterDrawer from './components/header/filters/DocumentAdvancedFilterDrawer.vue';
import DocumentFilterMenuMobile from './components/header/filters/DocumentFilterMenuMobile.vue';
import FavoriteFilterActionMobile from './components/header/filters/FavoriteFilterActionMobile.vue';
import AllFilterActionMobile from './components/header/filters/AllFilterActionMobile.vue';
import QuickFilterActionMobile from './components/header/filters/QuickFilterActionMobile.vue';
import AdvancedFilterActionMobile from './components/header/filters/AdvancedFilterActionMobile.vue';
import ExtendFilterActionMobile from './components/header/filters/ExtendFilterActionMobile.vue';
import UploadOverlay from './components/body/UploadOverlay.vue';
import VersionHistoryMenuAction from './components/body/actions/VersionHistoryMenuAction.vue';
import DocumentsSelectionCell from './components/body/table/cells/DocumentsSelectionCell.vue';
import DocumentActionContextMenu from './components/body/table/DocumentActionContextMenu.vue';
import DocumentsSizeCell from './components/body/table/cells/DocumentsSizeCell.vue';
import UploadNewVersionMenuAction from './components/body/actions/UploadNewVersionMenuAction.vue';
import PublicDocumentOptionsDrawer from './components/body/table/PublicDocumentOptionsDrawer.vue';
import DocumentImportFromZipDrawer from './components/header/actions/DocumentImportFromZipDrawer.vue';
import AttachmentsUploadInput from './components/header/actions/uploadZip/AttachmentsUploadInput.vue';
import AttachmentsUploaded from './components/header/actions/uploadZip/AttachmentsUploaded.vue';
import AttachmentsItem from './components/header/actions/uploadZip/AttachmentsItem.vue';
import DocumentsAddNewFileMenu from './components/header/actions/DocumentsAddNewFileMenu.vue';
import DocumentsMultiSelectMenu from './components/header/actions/DocumentsMultiSelectMenu.vue';



const components = {
  'documents-main': DocumentsMain,
  'documents-header': DocumentsHeader,
  'documents-header-left': DocumentsHeaderLeft,
  'documents-add-new-file': DocumentsAddNewFile,
  'documents-body': DocumentsBody,
  'documents-no-body': DocumentsNoBody,
  'documents-no-body-folder': DocumentsNoBodyFolder,
  'documents-no-result-body': DocumentsNoResultBody,
  'documents-folder-view': DocumentsFolderView,
  'documents-timeline-view': DocumentsTimelineView,
  'documents-timeline-group-header': DocumentsTimelineGroupHeader,
  'documents-table-cell': DocumentsTableCell,
  'documents-last-updated-cell': DocumentsLastUpdatedCell,
  'documents-visibility-cell': DocumentsVisibilityCell,
  'documents-file-size-cell': DocumentsFileSizeCell,
  'documents-last-activity-cell': DocumentsLastActivityCell,
  'documents-favorite-cell': DocumentsFavoriteCell,
  'documents-favorite-action': DocumentsFavoriteAction,
  'documents-file-name-cell': DocumentsFileNameCell,
  'documents-file-edit-name-cell': DocumentsFileEditNameCell,
  'documents-actions-menu': DocumentActionMenu,
  'documents-actions-menu-mobile': DocumentActionMenuMobile,
  'documents-add-new-menu-mobile': DocumentAddNewMobile,
  'documents-advanced-filter-drawer': DocumentAdvancedFilterDrawer,
  'documents-filter-menu-mobile': DocumentFilterMenuMobile,
  'documents-info-drawer': DocumentInfoDrawer,
  'documents-download-drawer': DocumentDownloadDrawer,
  'documents-select-period': SelectPeriod,
  'document-tree-selector-drawer': DocumentsTreeSelectorDrawer,
  'documents-move-spaces': DocumentsMoveSpaces,
  'documents-visibility-drawer': DocumentsVisibilityDrawer,
  'documents-visibility-all-users-drawer': DocumentsAllUsersVisibilityDrawer,
  'documents-visibility-collaborators': DocumentsVisibilityCollaborators,
  'documents-visibility-menu': DocumentsVisibilityMenu,
  'edit-menu-action': EditMenuAction,
  'rename-menu-action': RenameMenuAction,
  'download-menu-action': DownloadMenuAction,
  'open-location-menu-action': OpenLocationMenuAction,
  'open-read-only-menu-action': OpenReadOnlyMenuAction,
  'move-menu-action': MoveMenuAction,
  'duplicate-menu-action': DuplicateMenuAction,
  'visibility-menu-action': VisibilityMenuAction,
  'copy-link-menu-action': CopyLinkMenuAction,
  'favorite-menu-action': FavoriteMenuAction,
  'delete-menu-action': DeleteMenuAction,
  'shortcut-menu-action': ShortcutMenuAction,
  'documents-breadcrumb': DocumentsBreadcrumb,
  'folder-treeview-drawer': FolderTreeViewDrawer,
  'details-menu-action': DetailsMenuAction,
  'documents-info-details-cell': DocumentsInfoDetailsCell,
  'upload-overlay': UploadOverlay,
  'versionHistory-menu-action': VersionHistoryMenuAction,
  'favorite-filter-action': FavoriteFilterActionMobile,
  'all-filter-action': AllFilterActionMobile,
  'quick-filter-action': QuickFilterActionMobile,
  'mobile-advanced-filter-action': AdvancedFilterActionMobile,
  'extend-filter-action': ExtendFilterActionMobile,
  'documents-selection-cell': DocumentsSelectionCell,
  'document-action-context-menu': DocumentActionContextMenu,
  'documents-size-cell': DocumentsSizeCell,
  'upload-new-version-menu-action': UploadNewVersionMenuAction,
  'public-document-options-drawer': PublicDocumentOptionsDrawer,
  'document-import-from-zip-drawer': DocumentImportFromZipDrawer,
  'documents-zip-upload-input': AttachmentsUploadInput,
  'documents-zip-uploaded': AttachmentsUploaded,
  'documents-zip-item': AttachmentsItem,
  'documents-add-new-file-menu': DocumentsAddNewFileMenu,
  'documents-multi-select-menu': DocumentsMultiSelectMenu,
};

for (const key in components) {
  Vue.component(key, components[key]);
}
