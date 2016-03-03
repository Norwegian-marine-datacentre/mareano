// override 3.4.0 turn of spinning wheel("x-tree-node-loading") after node has been expanded to display node icon
Ext.override(Ext.tree.AsyncTreeNode, {
    expand : function(deep, anim, callback, scope){
        if(this.loading){ // if an async load is already running, waiting til it's done
            var timer;
            var f = function(){
                if(!this.loading){ // done loading
                    clearInterval(timer);
                    this.expand(deep, anim, callback, scope);
                }
            }.createDelegate(this);
            timer = setInterval(f, 200);
            return;
        }
        if(!this.loaded){
            if(this.fireEvent("beforeload", this) === false){
                return;
            }
            this.loading = true;
            this.ui.beforeLoad(this);
            var loader = this.loader || this.attributes.loader || this.getOwnerTree().getLoader();
            if(loader){
                loader.load(this, this.loadComplete.createDelegate(this, [deep, anim, callback, scope]), this);
                this.ui.afterLoad(this); //bugfix
                return;
            }
            this.ui.afterLoad(this); //bugfix
        }
        Ext.tree.AsyncTreeNode.superclass.expand.call(this, deep, anim, callback, scope);
    },
});